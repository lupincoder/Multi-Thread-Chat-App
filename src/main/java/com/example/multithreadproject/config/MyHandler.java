package com.example.multithreadproject.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyHandler extends TextWebSocketHandler {
    final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 1000,1000);
        sessions.add(safeSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (WebSocketSession s : sessions) {
            if (s.isOpen() && !s.equals(session)) {
                TextMessage newMessage = new TextMessage("The client sent: " + message.getPayload());
                s.sendMessage(newMessage);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) {
        sessions.remove(session);
        try { session.close(); } catch (Exception ignored) {}
    }
}
