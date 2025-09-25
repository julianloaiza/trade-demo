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
        SseEmitter emitter = new SseEmitter(0L); // no timeout for demo
        emitters.add(emitter);

        // Cleanup when client disconnects
        Runnable remove = () -> emitters.remove(emitter);
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(e -> remove.run());

        return emitter;
    }

    // Simulate "publishing to Kafka": here we just emit via SSE
    @Override
    public void sendDefault(String key, TradeMessage msg) {
        // In a POC, "best effort" is enough. If it fails, we remove the client.
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("trade").data(msg));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public int getSubscriberCount() {
        return emitters.size();
    }
}