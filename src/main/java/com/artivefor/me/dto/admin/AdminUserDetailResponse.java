package com.artivefor.me.dto.admin;

import lombok.*;
import java.util.List;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDetailResponse {
    private AdminUserResponse userInfo; // 아까 만든 기본 유저 정보

    // 유저와 연관된 리스트들
    private List<String> artworkTitles;   // 작품 제목 리스트
    private List<String> exhibitionNames; // 전시 명칭 리스트
    private List<String> awardNames;      // 수상 명칭 리스트
}