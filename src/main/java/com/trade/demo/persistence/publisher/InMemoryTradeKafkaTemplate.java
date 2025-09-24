package com.trade.demo.persistence.publisher;

import com.trade.demo.domain.infrastructure.KafkaTemplate;
import com.trade.demo.domain.model.TradeMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Component
public class InMemoryTradeKafkaTemplate implements KafkaTemplate<String, TradeMessage> {
    
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    @Override
    public void sendDefault(String key, TradeMessage msg) {
        // Simulate Kafka by broadcasting to SSE subscribers
        broadcastTrade(msg);
        System.out.println("Trade published to " + emitters.size() + " subscribers: " + msg.getTradeId());
    }
    
    /**
     * Add a new SSE emitter to the list of subscribers
     */
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        
        // Remove emitter when connection is closed
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));
        
        System.out.println("SSE subscriber connected: " + emitters.size());
        return emitter;
    }
    
    /**
     * Broadcast a trade message to all connected clients
     */
    public void broadcastTrade(TradeMessage trade) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("trade")
                    .data(trade));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        
        // Remove dead emitters
        emitters.removeAll(deadEmitters);
    }
    
    /**
     * Get the number of active subscribers
     */
    public int getSubscriberCount() {
        return emitters.size();
    }
}