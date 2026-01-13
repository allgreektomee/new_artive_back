package com.artivefor.me.dto.image;

public record ImageUploadResponse(
        String imageUrl,      // S3에 저장된 전체 URL (https://...)
        String originalName   // 사용자가 올린 원래 파일명
) {}
