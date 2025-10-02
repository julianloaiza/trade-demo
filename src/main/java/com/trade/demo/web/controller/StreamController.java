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
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class StreamController {
    
    private final KafkaTemplateImpl kafkaTemplate;
    private final TradeProcessor tradeProcessor;
    
    public StreamController(KafkaTemplateImpl kafkaTemplate, TradeProcessor tradeProcessor) {
        this.kafkaTemplate = kafkaTemplate;
        this.tradeProcessor = tradeProcessor;
    }
   
    // Check service health
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        kafkaTemplate.cleanupInactiveConnections();
        return ResponseEntity.ok("Trade Demo Service is running. Active subscribers: " + kafkaTemplate.getSubscriberCount());
    }
    
    // Start SSE stream
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTrades() {
        return kafkaTemplate.addEmitter();
    }
    
    // Inject FIX message
    @PostMapping("/fix")
    public ResponseEntity<String> injectFixMessage(@RequestBody Map<Integer, String> fixData) {
        try {
            Message message = new Message(fixData);
            tradeProcessor.onMessage(message);
            return ResponseEntity.ok("FIX message processed successfully");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing FIX message: " + e.getMessage());
        }
    }
}
