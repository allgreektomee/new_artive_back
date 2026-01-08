package com.artivefor.me.dto.admin;

import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.user.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder           // <--- 이게 빠져서 builder()를 찾지 못했던 것입니다!
@AllArgsConstructor // Builder를 사용하기 위해 필수
@NoArgsConstructor  // 기본 생성자
public class AdminUserResponse {
    private Long id;
    private String email;
    private String slug;
    private Role role;
    private LocalDateTime createdAt;

    // 엔티티를 받아서 DTO로 변환하는 핵심 로직
    public static AdminUserResponse fromEntity(ArtiveUser user) {
        // 프로필 내의 한국어(KO) 닉네임을 기본으로 가져오되, 없으면 이메일 앞자리 사용


        // 3. 이제 선언된 nickname 변수를 빌더에 넣습니다.
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .slug(user.getSlug())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}