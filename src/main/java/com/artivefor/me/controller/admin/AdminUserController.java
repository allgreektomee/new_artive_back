package com.artivefor.me.controller.admin;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.user.Role;
import com.artivefor.me.dto.admin.AdminUserDetailResponse;
import com.artivefor.me.dto.common.ApiResponse;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import com.artivefor.me.dto.admin.AdminUserResponse; // DTO 패키지도 맞춰주세요
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "관리자 - 유저 관리", description = "리액트 어드민 전용 유저 관리 API")
@RestController
@RequestMapping("/api/v1/admin/users") // 경로에 admin을 명시
@RequiredArgsConstructor
public class AdminUserController {

    private final ArtiveUserRepository userRepository;

    // 리액트 관리자 페이지 유저 목록 조회
    @GetMapping
    public ApiResponse<Page<AdminUserResponse>> getAllUsers(Pageable pageable) {
        // 유저 엔티티를 DTO로 변환하여 반환하는 로직 (Service에서 처리 권장)
        Page<AdminUserResponse> users = userRepository.findAll(pageable)
                .map(user -> AdminUserResponse.fromEntity(user));
        return ApiResponse.success(users, MessageCode.SUCCESS);
    }

    // 특정 유저 권한 변경 (USER -> ADMIN 등)
    @PatchMapping("/{id}/role")
    public ApiResponse<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        ArtiveUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 여기서 실제로 권한 업데이트 로직 수행
        // user.updateRole(role);
        // userRepository.save(user);

        return ApiResponse.success(MessageCode.SUCCESS);
    }

    // AdminUserController.java 내부에 추가
    @GetMapping("/{id}/detail")
    public ApiResponse<AdminUserDetailResponse> getUserDetail(@PathVariable Long id) {
        ArtiveUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 간단하게 제목들만 뽑아서 전달하는 예시
        return ApiResponse.success(AdminUserDetailResponse.builder()
                .userInfo(AdminUserResponse.fromEntity(user))
                .artworkTitles(user.getArtworks().stream().map(a -> "작품명...").toList())
                .build(),MessageCode.SUCCESS);
    }

}