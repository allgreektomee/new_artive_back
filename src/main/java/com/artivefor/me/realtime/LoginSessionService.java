package com.artivefor.me.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LoginSessionService {

    private final ObjectMapper objectMapper;

    /** 로그인 계정(JWT subject 등 식별자) → 서버가 인정하는 현재 로그인 세션 ID(숫자, 로그인할 때마다 증가). */
    private final Map<String, Long> emailToCurrentTestSessionId = new ConcurrentHashMap<>();
    /** 로그인 계정 → (해당 계정의 testSessionId → 그 ID로 연 WebSocket들). */
    private final Map<String, ConcurrentHashMap<Long, Set<WebSocketSession>>> socketsByEmailAndSessionId =
            new ConcurrentHashMap<>();

    public long getCurrentTestSessionId(String email) {
        return emailToCurrentTestSessionId.getOrDefault(email, 0L);
    }

    public boolean matchesCurrentTestSessionId(String email, long tokenTestSessionId) {
        return getCurrentTestSessionId(email) == tokenTestSessionId;
    }

    /**
     * 로그인 성공 직후 호출. 새 JWT {@code testSessionId} 값을 반환하고,
     * 직전 세션 ID로 붙어 있던 WebSocket에는 무효 알림 후 종료.
     */
    public long bumpTestSessionIdOnLogin(String email) {
        long previousId = emailToCurrentTestSessionId.getOrDefault(email, 0L);
        long newId = previousId + 1;
        emailToCurrentTestSessionId.put(email, newId);
        notifySuperseded(email, previousId);
        return newId;
    }

    private void notifySuperseded(String email, long previousTestSessionId) {
        ConcurrentHashMap<Long, Set<WebSocketSession>> bySessionId = socketsByEmailAndSessionId.get(email);
        if (bySessionId == null) {
            return;
        }
        Set<WebSocketSession> sessions = bySessionId.remove(previousTestSessionId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String payload = toJson(RealtimePushMessage.sessionSuperseded(
                "duplicate_login",
                "다른 곳에서 로그인되어 중복 로그인되었습니다."));
        for (WebSocketSession session : Set.copyOf(sessions)) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                    session.close(CloseStatus.GOING_AWAY.withReason("duplicate_login"));
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void registerSession(WebSocketSession session, String email, long testSessionId) {
        socketsByEmailAndSessionId
                .computeIfAbsent(email, e -> new ConcurrentHashMap<>())
                .computeIfAbsent(testSessionId, g -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void unregisterSession(WebSocketSession session, String email, Long testSessionId) {
        if (email == null || testSessionId == null) {
            return;
        }
        ConcurrentHashMap<Long, Set<WebSocketSession>> bySessionId = socketsByEmailAndSessionId.get(email);
        if (bySessionId == null) {
            return;
        }
        Set<WebSocketSession> set = bySessionId.get(testSessionId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                bySessionId.remove(testSessionId, set);
            }
        }
        if (bySessionId.isEmpty()) {
            socketsByEmailAndSessionId.remove(email, bySessionId);
        }
    }

    /**
     * 연결된 모든 WebSocket에 동일 JSON 전송 (연결 유지·테스트용).
     */
    public void broadcastToAllOpenConnections(String json) {
        for (ConcurrentHashMap<Long, Set<WebSocketSession>> bySessionId : socketsByEmailAndSessionId.values()) {
            for (Set<WebSocketSession> sessions : bySessionId.values()) {
                for (WebSocketSession session : Set.copyOf(sessions)) {
                    if (!session.isOpen()) {
                        continue;
                    }
                    try {
                        session.sendMessage(new TextMessage(json));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public String toJson(RealtimePushMessage msg) {
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"" + RealtimePushMessage.TYPE_SESSION_SUPERSEDED
                    + "\",\"message\":\"메시지 직렬화 오류\"}";
        }
    }
}
