package com.trade.demo.web.controller;

import com.trade.demo.domain.model.Message;
import com.trade.demo.domain.service.TradeProcessor;
import com.trade.demo.persistence.publisher.KafkaTemplateImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Controller for streaming trade data via Server-Sent Events (SSE)
 */
@RestController
@RequestMapping("/api")
public class StreamController {
    
    private final KafkaTemplateImpl kafkaTemplate;
    private final TradeProcessor tradeProcessor;
    
    public StreamController(KafkaTemplateImpl kafkaTemplate, TradeProcessor tradeProcessor) {
        this.kafkaTemplate = kafkaTemplate;
        this.tradeProcessor = tradeProcessor;
    }
    
    /**
     * SSE endpoint for real-time trade streaming
     * Frontend connects to this endpoint to receive trade updates
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTrades() {
        return kafkaTemplate.addEmitter();
    }
    
    /**
     * Health check endpoint to verify the service is running
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Trade Demo Service is running. Active subscribers: " + kafkaTemplate.getSubscriberCount());
    }
    
    /**
     * Endpoint to inject FIX messages for testing
     * Accepts JSON with FIX tags as integer keys
     */
    @PostMapping("/fix")
    public ResponseEntity<String> injectFixMessage(@RequestBody Map<Integer, String> fixData) {
        try {
            // Create Message object directly from JSON
            Message message = new Message(fixData);
            
            // Process the message through TradeProcessor
            tradeProcessor.onMessage(message);
            
            return ResponseEntity.accepted().body("FIX message processed successfully");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing FIX message: " + e.getMessage());
        }
    }
}
