package com.artivefor.me.realtime;

import com.artivefor.me.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTR_EMAIL = "email";
    /** WebSocketSession attributes 키 — JWT 클레임 testSessionId 와 동일한 값(Long). */
    public static final String ATTR_TEST_SESSION_ID = "testSessionId";

    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSessionService loginSessionService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        String token = servletRequest.getServletRequest().getParameter("token");
        if (token == null || token.isBlank() || !jwtTokenProvider.validateToken(token)) {
            return false;
        }
        try {
            String email = jwtTokenProvider.getEmail(token);
            long testSessionId = jwtTokenProvider.getTestSessionId(token);
            if (!loginSessionService.matchesCurrentTestSessionId(email, testSessionId)) {
                return false;
            }
            attributes.put(ATTR_EMAIL, email);
            attributes.put(ATTR_TEST_SESSION_ID, testSessionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
    }
}
