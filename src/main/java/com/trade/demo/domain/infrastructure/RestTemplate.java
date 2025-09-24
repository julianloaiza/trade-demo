package com.trade.demo.domain.infrastructure;

/**
 * Interface for REST template operations
 */
public interface RestTemplate {
    enum HttpMethod { GET, PUT, POST, DELETE }
    
    /**
     * Makes an HTTP REST Request
     */
    <R, T> T exchange(String url, HttpMethod method, R requestBody, Class<T> returnType);
}
