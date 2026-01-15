package com.artivefor.me.service.common;

import com.artivefor.me.data.common.SystemConfig;
import com.artivefor.me.data.common.UpdateStatus;
import com.artivefor.me.repository.common.SystemConfigRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemConfigService {
    private final SystemConfigRepository configRepository;

    public SystemConfig getSystemConfig() {
        // 데이터가 없을 경우를 대비해 기본값을 반환하거나 에러를 던집니다.
        return configRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("시스템 설정 데이터가 존재하지 않습니다."));
    }

    /**
     * 관리자 페이지에서 설정을 업데이트함
     */
    @Transactional
    public void updateSystemConfig(SystemConfig request) {
        SystemConfig config = getSystemConfig();

        // 빌더에서 만든 updateConfig 메서드나 개별 setter를 사용하여 값 교체
        config.updateConfig(
                request.isMaintenance(),
                request.getMaintenanceMessage(),
                request.getIosUpdateStatus(),
                request.getIosMinVersion(),
                request.getIosLatestVersion(),
                request.getIosStoreUrl(),
                request.getAosUpdateStatus(),
                request.getAosMinVersion(),
                request.getAosLatestVersion(),
                request.getAosStoreUrl(),
                request.isNoticeActive(),
                request.getNoticeTitle(),
                request.getNoticeContent()
        );
    }
}
