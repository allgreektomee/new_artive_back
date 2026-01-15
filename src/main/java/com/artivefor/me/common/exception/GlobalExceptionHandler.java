package com.artivefor.me.common.exception;

import com.artivefor.me.common.util.MessageUtil;
import com.artivefor.me.dto.common.ApiResponse;      // 👈 우리가 만든 공용 응답 DTO
import com.artivefor.me.common.util.MessageCode;    // 👈 우리가 만든 메시지 Enum (필요시)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        MessageCode mc = e.getMessageCode();
        // MessageUtil을 사용하여 실제 번역된 메시지를 가져옵니다.
        String translatedMessage = MessageUtil.getMessage(mc);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(translatedMessage)); // 프론트에 번역된 메시지 전달
    }

    // MethodArgumentNotValidException (DTO 유효성 검사 실패 시)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // 첫 번째 유효성 에러 메시지를 가져옵니다.
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }
}