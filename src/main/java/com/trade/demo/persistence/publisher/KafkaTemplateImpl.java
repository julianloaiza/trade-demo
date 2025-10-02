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

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Add new SSE connection with auto-cleanup
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(300000L);
        emitters.add(emitter);
        
        Runnable remove = () -> emitters.remove(emitter);
        
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(e -> remove.run());
        
        return emitter;
    }

    // Send message to all active connections
    @Override
    public void sendDefault(String key, TradeMessage msg) {        
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("trade").id(key).data(msg));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }

    // Remove inactive connections by sending ping
    public void cleanupInactiveConnections() {
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data("ping"));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }
    
    // Get current subscriber count
    public int getSubscriberCount() {
        return emitters.size();
    }
}