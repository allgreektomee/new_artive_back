package com.artivefor.me.service.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.artivefor.me.dto.image.ImageUploadResponse; // 직접 만드신 DTO 경로
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageUploadResponse uploadImage(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("파일이 없습니다.");

        // 1. 파일명 생성 (폴더 구조화: artworks/2026/01/uuid_name.jpg)
        String fileName = "artworks/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // 2. 메타데이터 설정 (이미지 타입 등)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 3. S3 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); // 누구나 읽을 수 있게 설정

            // 4. 업로드된 파일의 URL 가져오기
            String imageUrl = amazonS3.getUrl(bucket, fileName).toString();

            return new ImageUploadResponse(imageUrl, file.getOriginalFilename());

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }
}