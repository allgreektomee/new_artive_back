package com.artivefor.me.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 5초마다 연결된 소켓에 JSON 테스트 메시지 브로드캐스트.
 */
@Component
@RequiredArgsConstructor
public class RealtimeHeartbeatScheduler {

    private final LoginSessionService loginSessionService;

    @Scheduled(fixedRate = 5000)
    public void broadcastConnectionTest() {
        var msg = RealtimePushMessage.connectionTest("소켓 통신 테스트", System.currentTimeMillis());
        loginSessionService.broadcastToAllOpenConnections(loginSessionService.toJson(msg));
    }
}
