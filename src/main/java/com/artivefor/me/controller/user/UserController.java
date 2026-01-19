package com.artivefor.me.controller.user;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.common.util.MessageUtil;
import com.artivefor.me.dto.common.ApiResponse;
import com.artivefor.me.dto.user.ProfileDto;
import com.artivefor.me.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users") // 유저 데이터 관련 API
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. 내 프로필 조회 (로그인한 본인의 정보)
    @GetMapping("/profile")
    public ApiResponse<ProfileDto.Response> getMyProfile(Principal principal) {
        // principal.getName()을 통해 현재 접속한 유저의 이메일을 가져옴
        ProfileDto.Response response = userService.getProfile(principal.getName());
        return ApiResponse.success(response, MessageCode.PROFILE_GET_SUCCESS);
    }

    // 2. 내 프로필 수정
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(
            Principal principal,
            @RequestBody ProfileDto.UpdateRequest request) {
        userService.updateProfile(principal.getName(), request);
        return ApiResponse.success(MessageCode.USER_PROFILE_UPDATE_SUCCESS);
    }
}

/*

Principal: 현재 로그인한 유저의 이메일을 가져옵니다. (Security 설정 완료 시 사용 가능)

computeIfAbsent: 맵에 해당 언어(KO, EN 등) 자료가 있으면 가져오고, 없으면 새로 만들어서 넣어주는 아주 편리한 기능입니다.

다국어 확장성: 이제 프론트에서 language: "EN"으로 요청만 보내면, 영어 프로필도 즉시 저장됩니다.
 */