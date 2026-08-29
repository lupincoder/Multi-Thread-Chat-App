package com.example.multithreadproject.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyHandler extends TextWebSocketHandler {
    final ConcurrentHashMap<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomName = getRoomFromUri(Objects.requireNonNull(session.getUri()));
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 1000,1000);
        rooms.computeIfAbsent(roomName, k -> ConcurrentHashMap.newKeySet());
        rooms.get(roomName).add(safeSession);
        safeSession.sendMessage(new TextMessage("You are in room: " + roomName));
        System.out.println("Chatters: ");
        Set<WebSocketSession> chatters = rooms.get(roomName);
        for (WebSocketSession chat : chatters) {
            System.out.println("chatter: " + chat.getId());
        }
        System.out.println("Room: " + rooms.get(roomName));
        System.out.println("Number of chatters: " + chatters.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomName = getRoomFromUri(Objects.requireNonNull(session.getUri()));
        Set<WebSocketSession> chatters = rooms.get(roomName);
        if (chatters != null) {
            TextMessage broadcastText = new TextMessage("User " + session.getId() + " says: " + message.getPayload());
            for (WebSocketSession chatter : chatters) {
                if (chatter.isOpen() && !chatter.getId().equals(session.getId())) {
                    chatter.sendMessage(broadcastText);
                }

            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomName = getRoomFromUri(Objects.requireNonNull(session.getUri()));
        Set<WebSocketSession> chatters = rooms.get(roomName);
        if (chatters != null) {
            chatters.removeIf(s -> s.getId().equals(session.getId()));
            if (chatters.isEmpty()) {
                rooms.remove(roomName);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) throws Exception {
        try { session.close(); } catch (Exception ignored) {
            afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
        }
    }

    public String getRoomFromUri(URI uri) {
        String query = uri.getQuery();
        if (query == null || !query.startsWith("room=")) {
            throw new IllegalArgumentException("Invalid room query parameter");
        }
        return query.substring("room=".length());
    }
}
