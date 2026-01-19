package com.artivefor.me.controller.common;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.data.common.SystemConfig;
import com.artivefor.me.dto.common.ApiResponse;
import com.artivefor.me.service.common.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * 앱 사용자용: 초기 세팅 정보 조회
     * GET /api/v1/config
     */
    @GetMapping
    public ApiResponse<SystemConfig> getConfig() {

        return ApiResponse.success(systemConfigService.getSystemConfig(), MessageCode.SUCCESS);
    }

    /**
     * 관리자용: 설정값 변경
     * PATCH /api/v1/config/info
     */
    @PatchMapping("/info")
    public ApiResponse<Void> updateConfig(@RequestBody SystemConfig config) {
        systemConfigService.updateSystemConfig(config);
        return ApiResponse.success(MessageCode.SUCCESS);
    }
}