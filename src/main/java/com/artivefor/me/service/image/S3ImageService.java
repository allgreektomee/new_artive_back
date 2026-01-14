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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // 예: admin01/artwork/20260114_153022_0.jpg, admin01/artwork/20260114_153022_1.jpg
    public List<ImageUploadResponse> uploadImages(List<MultipartFile> files, String userId, String category) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String extension = getExtension(file.getOriginalFilename());

            // 파일명: {유저ID}/{카테고리}/{날짜시간}_{순번}{확장자}
            String fileName = String.format("%s/%s/%s_%d%s", userId, category, timestamp, i, extension);

            try {
                // S3 업로드 로직 (기존과 동일)
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), new ObjectMetadata()));


                responses.add(new ImageUploadResponse(amazonS3.getUrl(bucket, fileName).toString(), file.getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }
        return responses;
    }
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ""; // 확장자가 없는 경우
        }
        // 마지막 '.'의 위치 이후부터 끝까지 잘라냄 (예: image.jpg -> .jpg)
        return fileName.substring(fileName.lastIndexOf("."));
    }
}