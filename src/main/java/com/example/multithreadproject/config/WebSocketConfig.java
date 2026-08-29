package com.example.multithreadproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    MyHandler myHandler;

    public WebSocketConfig(MyHandler myHandler) {
        this.myHandler = myHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler, "/ws")
                .setAllowedOrigins("*");
    }

}
