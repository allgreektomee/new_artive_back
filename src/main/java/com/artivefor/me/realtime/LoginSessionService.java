package com.artivefor.me.realtime;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginSessionService {

    private final Map<String, Long> emailToGeneration = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentHashMap<Long, Set<WebSocketSession>>> socketsByEmailGen = new ConcurrentHashMap<>();

    public long getCurrentGeneration(String email) {
        return emailToGeneration.getOrDefault(email, 0L);
    }

    public boolean matchesCurrentGeneration(String email, long tokenGeneration) {
        return getCurrentGeneration(email) == tokenGeneration;
    }

    /**
     * 로그인 성공 직후, 새 JWT에 넣을 세대 값. 이전 세대 WebSocket에 무효 알림 전송.
     */
    public long bumpGenerationOnLogin(String email) {
        long oldGen = emailToGeneration.getOrDefault(email, 0L);
        long newGen = oldGen + 1;
        emailToGeneration.put(email, newGen);
        notifySuperseded(email, oldGen);
        return newGen;
    }

    private void notifySuperseded(String email, long oldGen) {
        ConcurrentHashMap<Long, Set<WebSocketSession>> byGen = socketsByEmailGen.get(email);
        if (byGen == null) {
            return;
        }
        Set<WebSocketSession> sessions = byGen.remove(oldGen);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String payload = "{\"type\":\"SESSION_SUPERSEDED\",\"reason\":\"duplicate_login\"}";
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

    public void registerSession(WebSocketSession session, String email, long loginGeneration) {
        socketsByEmailGen
                .computeIfAbsent(email, e -> new ConcurrentHashMap<>())
                .computeIfAbsent(loginGeneration, g -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void unregisterSession(WebSocketSession session, String email, Long loginGeneration) {
        if (email == null || loginGeneration == null) {
            return;
        }
        ConcurrentHashMap<Long, Set<WebSocketSession>> byGen = socketsByEmailGen.get(email);
        if (byGen == null) {
            return;
        }
        Set<WebSocketSession> set = byGen.get(loginGeneration);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                byGen.remove(loginGeneration, set);
            }
        }
        if (byGen.isEmpty()) {
            socketsByEmailGen.remove(email, byGen);
        }
    }
}
