package com.artivefor.me.controller.auth;

import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.auth.AuthRequest;
import com.artivefor.me.dto.auth.TokenResponse;
import com.artivefor.me.security.jwt.JwtTokenProvider;
import com.artivefor.me.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 1. 이메일 인증 번호 전송
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmailCode(@RequestBody AuthRequest.EmailSend request) {
        try {
            authService.sendCode(request.getEmail());
            return ResponseEntity.ok("인증 번호가 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("발송 실패: " + e.getMessage());
        }
    }

    // 2. 인증 번호 확인
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyCode(@RequestBody AuthRequest.EmailVerify request) {
        if (authService.verifyCode(request.getEmail(), request.getCode())) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(401).body("인증 실패");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.Login request) {
        try {
            // 2. AuthService를 통해 유저 검증 및 정보 가져오기
            ArtiveUser user = authService.login(request.getEmail(), request.getPassword());

            // 3. 토큰 생성
            String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(new TokenResponse(accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}