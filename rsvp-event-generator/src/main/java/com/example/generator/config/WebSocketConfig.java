package com.example.generator.config;

import com.example.generator.handler.RsvpWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RsvpWebSocketHandler rsvpWebSocketHandler;

    public WebSocketConfig(RsvpWebSocketHandler rsvpWebSocketHandler) {
        this.rsvpWebSocketHandler = rsvpWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rsvpWebSocketHandler, "/rsvp").setAllowedOrigins("*");
    }
}
