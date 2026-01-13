package com.artivefor.me.dto.common;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.common.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;   // 성공 여부
    private String message;    // 결과 메시지
    private T data;            // 실제 데이터 (없을 경우 null)

    // 성공 응답 편의 메서드 (데이터 포함)
    public static <T> ApiResponse<T> success(T data, MessageCode code) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(MessageUtil.getMessage(code))
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(MessageCode code) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(MessageUtil.getMessage(code)) // 여기서 번역 수행
                .data(null)
                .build();
    }



    // 실패 응답 편의 메서드
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}