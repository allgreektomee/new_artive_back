package com.artivefor.me.data.common;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemConfig extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 시스템 점검 설정 ---
    @Builder.Default
    private boolean isMaintenance = false;
    private String maintenanceMessage;

    // --- iOS 관련 설정 ---
    @Enumerated(EnumType.STRING)
    private UpdateStatus iosUpdateStatus;
    private String iosMinVersion;        // 강제 업데이트 기준
    private String iosLatestVersion;     // 스토어 최신 버전
    private String iosStoreUrl;

    // --- Android 관련 설정 ---
    @Enumerated(EnumType.STRING)
    private UpdateStatus aosUpdateStatus;
    private String aosMinVersion;
    private String aosLatestVersion;
    private String aosStoreUrl;

    // --- 공지사항 설정 ---
    @Builder.Default
    private boolean isNoticeActive = false;
    private String noticeTitle;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;

    /**
     * 비즈니스 로직: 설정 일괄 업데이트
     * (관리자 페이지에서 폼 데이터를 받아 한 번에 수정할 때 사용)
     */
    public void updateConfig(boolean isMaintenance, String maintenanceMessage,
                             UpdateStatus iosStatus, String iosMin, String iosLat, String iosUrl,
                             UpdateStatus aosStatus, String aosMin, String aosLat, String aosUrl,
                             boolean noticeActive, String noticeTitle, String noticeContent) {
        this.isMaintenance = isMaintenance;
        this.maintenanceMessage = maintenanceMessage;
        this.iosUpdateStatus = iosStatus;
        this.iosMinVersion = iosMin;
        this.iosLatestVersion = iosLat;
        this.iosStoreUrl = iosUrl;
        this.aosUpdateStatus = aosStatus;
        this.aosMinVersion = aosMin;
        this.aosLatestVersion = aosLat;
        this.aosStoreUrl = aosUrl;
        this.isNoticeActive = noticeActive;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
    }
}