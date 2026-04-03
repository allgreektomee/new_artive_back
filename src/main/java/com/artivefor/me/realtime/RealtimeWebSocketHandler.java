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
        Object idObj = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_TEST_SESSION_ID);
        if (email != null && idObj instanceof Long testSessionId) {
            loginSessionService.registerSession(session, email, testSessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String email = (String) session.getAttributes().get(JwtHandshakeInterceptor.ATTR_EMAIL);
        Object idObj = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_TEST_SESSION_ID);
        Long testSessionId = idObj instanceof Long l ? l : null;
        loginSessionService.unregisterSession(session, email, testSessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    }
}
