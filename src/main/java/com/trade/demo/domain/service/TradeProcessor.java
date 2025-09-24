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
        // Write code here
    }
}
