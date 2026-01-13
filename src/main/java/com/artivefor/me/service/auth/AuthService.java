package com.artivefor.me.service.auth;

import com.artivefor.me.common.exception.BusinessException;
import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.user.Role;
import com.artivefor.me.data.user.UserProfile;
import com.artivefor.me.data.user.UserSettings;
import com.artivefor.me.dto.auth.AuthRequest;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 유저를 찾아서 반환 (ArtiveUser는 이미 UserDetails를 구현했으므로 바로 반환 가능)
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + email));
    }

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

    @Transactional
    public void signUp(AuthRequest.SignUp request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            // 우리가 만든 비즈니스 예외를 던짐 -> GlobalExceptionHandler가 낚아챔
            throw new BusinessException(MessageCode.AUTH_ALREADY_EXIST_EMAIL);
        }

        // 1. 유저 본체 생성
        ArtiveUser user = ArtiveUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .slug(request.getNickname()) // 닉네임을 초기 슬러그로 사용
                .role(Role.USER)
                .build();

        // 관계 맺기
        // 2. 프로필 생성 (빌더 사용)
        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        user.setProfile(profile);

        // 3. 설정 생성 (빌더 사용)
        UserSettings settings = UserSettings.builder()
                .user(user)
                .build();
        user.setSettings(settings);

        // 이 부분은 컨트롤러 작업 전, 서비스 단에서 미리 객체를 생성해 두는 로직입니다.
        userRepository.save(user); // CascadeType.ALL에 의해 연관된 객체들이 함께 저장됩니다.
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