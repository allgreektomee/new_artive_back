package com.artivefor.me.common.exception;

import com.artivefor.me.dto.common.ApiResponse;      // 👈 우리가 만든 공용 응답 DTO
import com.artivefor.me.common.util.MessageCode;    // 👈 우리가 만든 메시지 Enum (필요시)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 우리가 정의한 커스텀 예외 처리 (예: 유저 없음, 비밀번호 틀림 등)
    // 별도의 BusinessException을 만들어 관리하면 좋습니다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        // e.getMessageCode()를 통해 Enum을 꺼내온다고 가정
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("error"));
    }

    // 2. 그 외 예상치 못한 런타임 에러
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        // 공통 에러 코드를 만들어서 던지거나, 메시지만 담아서 응답
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(e.getMessage())); // 이 땐 직접 메시지 전달용 사용
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("입력값이 올바르지 않습니다."));
    }
}