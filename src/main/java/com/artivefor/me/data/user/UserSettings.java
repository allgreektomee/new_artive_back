package com.artivefor.me.data.user;

import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSettings {

    @Id
    private Long id;

    @OneToOne @MapsId
    @JoinColumn(name = "user_id")
    private ArtiveUser user;

    /**
     * 사용자가 서비스를 이용할 기본 언어 (KO, EN 등)
     * 이 설정에 따라 대시보드나 메뉴의 언어가 결정됩니다.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LanguageCode preferredLanguage = LanguageCode.KO;

    @Builder.Default
    // 그 외 앱 사용에 필요한 설정들 (예: 알림 여부)
    private boolean emailNotifications = true;
}