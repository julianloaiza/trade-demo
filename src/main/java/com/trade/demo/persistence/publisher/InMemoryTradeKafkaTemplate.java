package com.trade.demo.persistence.publisher;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.model.TradeMessage;
import org.springframework.stereotype.Component;

@Component
public class InMemoryTradeKafkaTemplate implements KafkaTemplate<String, TradeMessage> {
    
    @Override
    public void sendDefault(String key, TradeMessage msg) {
        System.out.println("=== KAFKA SIMULATION ===");
        System.out.println("Key: " + key);
        System.out.println("TradeMessage: " + msg);
        System.out.println("Sending to default Kafka topic...");
        // Write logic here
        System.out.println("=========================");
    }
}