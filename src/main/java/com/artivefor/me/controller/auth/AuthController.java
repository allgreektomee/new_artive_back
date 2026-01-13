package com.artivefor.me.controller.auth;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.auth.AuthRequest;
import com.artivefor.me.dto.auth.TokenResponse;
import com.artivefor.me.dto.common.ApiResponse;
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

    // 1. 회원가입 (추가됨)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody AuthRequest.SignUp request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_SIGNUP_SUCCESS));
    }

    // 2. 이메일 인증 번호 전송 (개선)
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(@RequestBody AuthRequest.EmailSend request) {
        authService.sendCode(request.getEmail());
        // MessageCode.Auth에 EMAIL_SEND_SUCCESS("auth.email.send.success") 추가 필요
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_EMAIL_SEND_SUCCESS));
    }

    // 3. 인증 번호 확인 (개선)
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody AuthRequest.EmailVerify request) {
        authService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_EMAIL_VERIFY_SUCCESS));
    }

    // 4. 로그인 (개선)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody AuthRequest.Login request) {
        ArtiveUser user = authService.login(request.getEmail(), request.getPassword());
        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(new TokenResponse(accessToken), MessageCode.AUTH_LOGIN_SUCCESS));
    }
}