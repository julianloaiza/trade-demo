package com.trade.demo.persistence.security;

import com.trade.demo.domain.infrastructure.RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class SecurityMasterStubRestTemplate implements RestTemplate {
    
    @Override
    public <R, T> T exchange(String url, HttpMethod method, R requestBody, Class<T> returnType) {
        System.out.println("=== REST API SIMULATION ===");
        System.out.println("URL: " + url);
        System.out.println("Method: " + method);
        System.out.println("Request Body: " + requestBody);
        System.out.println("Return Type: " + returnType.getSimpleName());
        // Write logic here
        System.out.println("=========================");
        return null;
    }
}