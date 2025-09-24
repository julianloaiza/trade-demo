package com.trade.demo.persistence.security;

import com.trade.demo.domain.infrastructure.RestTemplate;
import com.trade.demo.domain.model.SecurityId;
import org.springframework.stereotype.Component;

@Component
public class SecurityMasterStubRestTemplate implements RestTemplate {
    
    @Override
    public <R, T> T exchange(String url, HttpMethod method, R requestBody, Class<T> returnType) {
        // Simple mock - return enriched SecurityId with all fields filled
        SecurityId enriched = new SecurityId(
            "AAPL.O",                    // RIC
            "US0378331005",              // ISIN
            "037833100",                 // CUSIP
            "2046251",                   // SEDOL
            "AAPL",                      // Ticker
            "Apple Inc."                 // Name
        );
        
        return (T) enriched;
    }
}