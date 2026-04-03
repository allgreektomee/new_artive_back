package com.artivefor.me.realtime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * WebSocket 텍스트 프레임에 실리는 JSON 공통 형식 (클라이언트는 type으로 분기).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RealtimePushMessage(
        String type,
        String message,
        String reason,
        Long timestamp
) {
    public static final String TYPE_CONNECTION_TEST = "CONNECTION_TEST";
    public static final String TYPE_SESSION_SUPERSEDED = "SESSION_SUPERSEDED";

    public static RealtimePushMessage connectionTest(String message, long timestampMillis) {
        return new RealtimePushMessage(TYPE_CONNECTION_TEST, message, null, timestampMillis);
    }

    public static RealtimePushMessage sessionSuperseded(String reason, String message) {
        return new RealtimePushMessage(TYPE_SESSION_SUPERSEDED, message, reason, null);
    }
}
