package com.trade.demo.domain.infrastructure;

import com.trade.demo.domain.model.TradeMessage;

/**
 * Interface for Kafka template operations
 */
public interface KafkaTemplate<String, TradeMessage> {
    /**
     * Sends TradeMessage to default Kafka topic.
     */
    void sendDefault(String key, TradeMessage msg);
}
