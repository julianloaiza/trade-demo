package com.trade.demo.web.config;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.infrastructure.RestTemplate;
import com.trade.demo.domain.model.TradeMessage;
import com.trade.demo.persistence.publisher.InMemoryTradeKafkaTemplate;
import com.trade.demo.persistence.security.SecurityMasterStubRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public KafkaTemplate<String, TradeMessage> kafkaTemplate() {
        return new InMemoryTradeKafkaTemplate();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new SecurityMasterStubRestTemplate();
    }
}