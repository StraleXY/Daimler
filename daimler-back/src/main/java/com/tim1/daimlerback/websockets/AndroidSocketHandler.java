package com.tim1.daimlerback.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

@Component
public class AndroidSocketHandler extends AbstractWebSocketHandler {

    WebSocketHandlerRegistry registry;

    private HashMap<Integer, WebSocketSession> sessionMap;

    public AndroidSocketHandler() {
        this.sessionMap = new HashMap<Integer, WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(Integer.parseInt(session.getHandshakeHeaders().get("id").get(0)), session);
        System.out.println("STARTED: " + Integer.parseInt(session.getHandshakeHeaders().get("id").get(0)));
        super.afterConnectionEstablished(session);
    }

    // If android ever chooses to send a message, remove comments
    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    //     String payload = message.getPayload();
    //     session.sendMessage(new TextMessage("Hi"));
    //     super.handleTextMessage(session, message);
    // }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(Integer.parseInt(session.getHandshakeHeaders().get("id").get(0)));
        System.out.println("REMOVED: " + Integer.parseInt(session.getHandshakeHeaders().get("id").get(0)));
        super.afterConnectionClosed(session, status);
    }

    public boolean sendMessage(Integer id, String message) {
        WebSocketSession session = this.sessionMap.get(id);
        if (session == null) return false;
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
