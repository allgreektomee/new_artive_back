package com.artivefor.me;

import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.user.Role;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArtiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtiveApplication.class, args);
	}

	// 여기에 추가합니다!
	@Bean
	CommandLineRunner initData(ArtiveUserRepository userRepository) {
		return args -> {
			// DB에 유저가 하나도 없을 때만 실행
			if (userRepository.count() == 0) {
				ArtiveUser admin = ArtiveUser.builder()
						.email("admin@artive.com")
						.role(Role.ADMIN)
						.slug("admin-user") // 필수 필드가 있다면 채워주세요
						.build();
				userRepository.save(admin);
				System.out.println("✅ 테스트용 관리자 계정이 생성되었습니다. (admin@artive.com)");
			}
		};
	}
}