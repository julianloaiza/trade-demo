package com.trade.demo.web.config;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.infrastructure.RestTemplate;
import com.trade.demo.domain.model.TradeMessage;
import com.trade.demo.persistence.publisher.KafkaTemplateImpl;
import com.trade.demo.persistence.security.RestTemplateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public KafkaTemplate<String, TradeMessage> kafkaTemplate() {
        return new KafkaTemplateImpl();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateImpl();
    }
}