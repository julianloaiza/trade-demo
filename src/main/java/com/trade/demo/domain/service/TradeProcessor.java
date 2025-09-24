package com.trade.demo.domain.service;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.infrastructure.RestTemplate;
import com.trade.demo.domain.model.Message;
import com.trade.demo.domain.model.SecurityId;
import com.trade.demo.domain.model.TradeMessage;
import com.trade.demo.domain.enums.IdSource;
import org.springframework.stereotype.Component;

/**
 * TradeProcessor component that handles incoming FIX messages
 */
@Component
public class TradeProcessor {
    
    private final KafkaTemplate<String, TradeMessage> kafkaTemplate;
    private final RestTemplate restTemplate;
    
    public TradeProcessor(KafkaTemplate<String, TradeMessage> kafkaTemplate, 
                         RestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }
    
    /**
     * Handles incoming FIX messages.
     */
    public void onMessage(Message msg) {
        String execId = msg.getString(17);
        System.out.println("Processing trade: " + execId);
        
        // Extract FIX tags
        String securityId = msg.getString(48);        // Security ID
        String idSourceStr = msg.getString(22);      // Security ID Source
        String account = msg.getString(1);           // Account
        Integer lastShares = msg.getInt(32);          // Last Shares (positive)
        Double avgPx = msg.getDouble(6);              // Average Price (positive)
        String side = msg.getString(8);              // Side (BUY/SELL)
        
        // Map IdSource string to enum
        IdSource idSource = IdSource.valueOf(idSourceStr);
        
        // Calculate quantity with sign (BUY=+, SELL=-)
        Integer qty = "BUY".equals(side) ? lastShares : -lastShares;
        
        // Create TradeMessage
        TradeMessage tradeMessage = new TradeMessage();
        tradeMessage.setTradeId(execId);
        tradeMessage.setAccount(account);
        tradeMessage.setSecurityId(securityId);
        tradeMessage.setIdSource(idSource);
        tradeMessage.setQty(qty);
        tradeMessage.setPrice(avgPx);
        
        // Enrich with Security Master data
        TradeMessage enrichedTrade = enrichWithSecurityMaster(tradeMessage);
        
        // Generate Kafka key and send
        String kafkaKey = execId + account;
        kafkaTemplate.sendDefault(kafkaKey, enrichedTrade);
        
        System.out.println("Trade processed: " + execId);
    }
    
    private TradeMessage enrichWithSecurityMaster(TradeMessage trade) {
        // Create simple SecurityId request
        SecurityId request = new SecurityId();
        request.setRic(trade.getSecurityId()); // Simple request with RIC
        
        // Call Security Master API
        SecurityId enrichedSecurity = restTemplate.exchange(
            "https://sec-master.bns/find",
            RestTemplate.HttpMethod.POST,
            request,
            SecurityId.class
        );
        
        // Update trade with enriched security data
        trade.setRic(enrichedSecurity.getRic());
        trade.setIsin(enrichedSecurity.getIsin());
        trade.setCusip(enrichedSecurity.getCusip());
        trade.setSedol(enrichedSecurity.getSedol());
        trade.setTicker(enrichedSecurity.getTicker());
        
        return trade;
    }
}
