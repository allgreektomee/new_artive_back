package com.artivefor.me.data.artwork;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkStatus {

    IN_PROGRESS("작업 중"),
    COMPLETED("완성"),
    FOR_SALE("판매 중"),
    SOLD_OUT("판매 완료");

    private final String description; // 한국어 설명을 붙여두면 나중에 화면에 보여주기 편합니다.
}