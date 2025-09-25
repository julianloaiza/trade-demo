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
    
    // Main entry point
    public void onMessage(Message fix) {
        String execId = fix.getString(17);
        System.out.println("Processing trade: " + execId);
        
        TradeMessage trade = mapFixToTrade(fix);
        enrichWithSecurityMaster(trade);
        kafkaTemplate.sendDefault(trade.getTradeId(), trade);
        
        System.out.println("Trade processed: " + execId);
    }
    
    // Mapper - extracts FIX data to TradeMessage
    private TradeMessage mapFixToTrade(Message fix) {
        String execId = fix.getString(17);
        String securityId = fix.getString(48);
        String idSourceStr = fix.getString(22);
        String account = fix.getString(1);
        Integer lastShares = fix.getInt(32);
        Double avgPx = fix.getDouble(6);
        String side = fix.getString(8);
        
        // Calculate quantity with sign (BUY=+, SELL=-)
        Integer qty = "BUY".equals(side) ? lastShares : -lastShares;
        
        TradeMessage trade = new TradeMessage();
        trade.setTradeId(execId);
        trade.setAccount(account);
        trade.setSecurityId(securityId);
        trade.setIdSource(IdSource.valueOf(idSourceStr));
        trade.setQty(qty);
        trade.setPrice(avgPx);
        
        return trade;
    }
    
    // Enrich trade with Security Master data
    private void enrichWithSecurityMaster(TradeMessage trade) {
        SecurityId request = new SecurityId();
        request.setRic(trade.getSecurityId());
        
        SecurityId enriched = restTemplate.exchange(
            "https://sec-master.bns/find",
            RestTemplate.HttpMethod.POST,
            request,
            SecurityId.class
        );
        
        // Update trade with enriched data
        trade.setRic(enriched.getRic());
        trade.setIsin(enriched.getIsin());
        trade.setCusip(enriched.getCusip());
        trade.setSedol(enriched.getSedol());
        trade.setTicker(enriched.getTicker());
    }
}
