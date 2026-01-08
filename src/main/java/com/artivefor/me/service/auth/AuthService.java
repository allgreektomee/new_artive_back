package com.artivefor.me.service.auth;

import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.auth.AuthRequest;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    // 이메일을 열쇠(Key)로, 인증번호를 값(Value)으로 저장하는 지도(Map)
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder; // 스프링이 주입해줌
    private final ArtiveUserRepository userRepository;


    // 임시 저장소 (이메일을 키로, 인증번호를 값으로 저장)
    // 실제 운영 시에는 Redis를 사용하는 것이 좋지만, 우선 메모리 방식으로 구현합니다.
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>();

    // 1. 인증 번호 생성 및 메일 발송
//    public void sendCode(String email) throws Exception {
//        String code = emailService.createCode(); // 6자리 랜덤 번호 생성
//        verificationStorage.put(email, code);    // 메모리에 저장
//        emailService.sendVerificationEmail(email, code); // 실제 메일 발송
//    }

    public void sendCode(String email) {
        String code = "123456"; // 테스트용 고정 번호
        codeStorage.put(email, code);
        System.out.println("보안상 메일 발송은 생략! 테스트용 번호: " + code);
    }
    // 2. 사용자가 입력한 번호 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationStorage.get(email);

        if (savedCode != null && savedCode.equals(code)) {
            verificationStorage.remove(email); // 인증 성공 시 보안을 위해 삭제
            return true;
        }
        return false;
    }


    public void signUp(AuthRequest.SignUp request) {
        // 비밀번호 암호화해서 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        ArtiveUser user = ArtiveUser.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .build();

        userRepository.save(user);
    }

    public ArtiveUser login(String email, String password) {
        // 1. 이메일로 유저 찾기
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 확인 (암호화된 비번 vs 입력 비번)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}