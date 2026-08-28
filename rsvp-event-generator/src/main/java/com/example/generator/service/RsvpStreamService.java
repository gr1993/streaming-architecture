package com.example.generator.service;

import com.example.generator.model.RsvpEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RsvpStreamService {

    private final RandomRsvpGeneratorService generatorService;
    private final com.example.generator.handler.RsvpWebSocketHandler webSocketHandler;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService executorService;

    @Value("${generator.events-per-second:5}")
    private int eventsPerSecond;

    public RsvpStreamService(RandomRsvpGeneratorService generatorService, com.example.generator.handler.RsvpWebSocketHandler webSocketHandler) {
        this.generatorService = generatorService;
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        long period = 1000 / (eventsPerSecond > 0 ? eventsPerSecond : 1);
        executorService.scheduleAtFixedRate(this::sendEvent, 0, period, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    private void sendEvent() {
        RsvpEvent event = generatorService.generateRandomEvent();
        
        // Broadcast via WebSocket
        webSocketHandler.broadcastEvent(event);

        // Broadcast via SSE
        if (emitters.isEmpty()) {
            return;
        }
        
        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(event, org.springframework.http.MediaType.APPLICATION_JSON).name("message"));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
