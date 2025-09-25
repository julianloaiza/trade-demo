package com.trade.demo.persistence.publisher;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.model.TradeMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class KafkaTemplateImpl implements KafkaTemplate<String, TradeMessage> {

    // SSE subscribers
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Simple API for Controller to add a subscriber
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minute timeout
        emitters.add(emitter);

        // Cleanup when client disconnects
        Runnable remove = () -> {
            emitters.remove(emitter);
            System.out.println("SSE connection removed. Active subscribers: " + emitters.size());
        };
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(e -> {
            System.out.println("SSE connection error: " + e.getMessage());
            remove.run();
        });

        System.out.println("New SSE connection added. Active subscribers: " + emitters.size());
        return emitter;
    }

    // Simulate "publishing to Kafka": here we just emit via SSE
    @Override
    public void sendDefault(String key, TradeMessage msg) {
        // In a POC, "best effort" is enough. If it fails, we remove the client.
        List<SseEmitter> toRemove = new CopyOnWriteArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("trade").data(msg));
            } catch (IOException e) {
                toRemove.add(emitter);
            }
        }
        
        // Remove failed emitters after iteration
        emitters.removeAll(toRemove);
    }

    public int getSubscriberCount() {
        return emitters.size();
    }
    
    // Method to manually clean up inactive connections
    public void cleanupInactiveConnections() {
        List<SseEmitter> toRemove = new CopyOnWriteArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                // Try to send a ping to check if connection is alive
                emitter.send(SseEmitter.event().name("ping").data("ping"));
            } catch (IOException e) {
                toRemove.add(emitter);
            }
        }
        
        emitters.removeAll(toRemove);
        if (!toRemove.isEmpty()) {
            System.out.println("Cleaned up " + toRemove.size() + " inactive connections. Active subscribers: " + emitters.size());
        }
    }
}