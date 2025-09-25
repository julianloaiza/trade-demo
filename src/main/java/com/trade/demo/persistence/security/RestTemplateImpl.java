package com.trade.demo.persistence.security;

import com.trade.demo.domain.infrastructure.RestTemplate;
import com.trade.demo.domain.model.SecurityId;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateImpl implements RestTemplate {
    
    @Override
    public <R, T> T exchange(String url, HttpMethod method, R requestBody, Class<T> returnType) {
        // Simple mock - return enriched SecurityId with generic values
        SecurityId enriched = new SecurityId(
            "SEC001.O",                  // RIC
            "US1234567890",              // ISIN
            "123456789",                 // CUSIP
            "1234567",                   // SEDOL
            "SEC001",                    // Ticker
            "Security Name"              // Name
        );
        
        return (T) enriched;
    }
}