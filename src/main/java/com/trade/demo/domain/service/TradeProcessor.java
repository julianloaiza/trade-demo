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
        String securityId = msg.getString(48);        // securityId
        String idSourceStr = msg.getString(22);      // securityIdSource
        String account = msg.getString(1);            // account
        Integer lastShares = msg.getInt(32);          // lastShares (positive)
        Double avgPx = msg.getDouble(6);              // avgPx (positive)
        String side = msg.getString(8);              // side (BUY/SELL)
        
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
        enrichWithSecurityMaster(tradeMessage);
        
        // Send to Kafka
        String kafkaKey = execId;
        kafkaTemplate.sendDefault(kafkaKey, tradeMessage);
        
        System.out.println("Trade processed: " + execId);
    }
    
    private void enrichWithSecurityMaster(TradeMessage trade) {
        // Create SecurityId request with minimal data
        SecurityId request = new SecurityId();
        request.setRic(trade.getSecurityId());
        
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
    }
}
