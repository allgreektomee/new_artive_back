package com.artivefor.me.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class RealtimeWebSocketHandler extends TextWebSocketHandler {

    private final LoginSessionService loginSessionService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String email = (String) session.getAttributes().get(JwtHandshakeInterceptor.ATTR_EMAIL);
        Object genObj = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_LOGIN_GEN);
        if (email != null && genObj instanceof Long loginGen) {
            loginSessionService.registerSession(session, email, loginGen);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String email = (String) session.getAttributes().get(JwtHandshakeInterceptor.ATTR_EMAIL);
        Object genObj = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_LOGIN_GEN);
        Long loginGen = genObj instanceof Long l ? l : null;
        loginSessionService.unregisterSession(session, email, loginGen);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    }
}
