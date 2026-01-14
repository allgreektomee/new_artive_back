package com.artivefor.me.controller.image;

import com.artivefor.me.dto.image.ImageUploadResponse;
import com.artivefor.me.service.image.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3ImageService s3ImageService;

    /**
     * 공통 이미지 업로드 API
     * 어디서든 이미지가 필요하면 이 API를 호출해서 URL을 먼저 받아감
     */
    @PostMapping("/upload")
    public ResponseEntity<List<ImageUploadResponse>> uploadImage(
            @RequestParam("file") List<MultipartFile> files, // 여러 파일 대응
            @RequestParam("category") String category,       // "artwork", "history" 등
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();

        // 만약 유저 아이디에서 @ 같은 특수문자를 빼고 싶다면 가공
        String safeUserId = userId.split("@")[0];

        return ResponseEntity.ok(s3ImageService.uploadImages(files, safeUserId, category));
    }
}