package com.artivefor.me.data.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UpdateStatus {

    NORMAL,    // 직접 작성 (사진 위주)
    REQUIRED,   // 유튜브 영상 연동
    AVAILABLE   // 페이스북 포스트 연동
}


