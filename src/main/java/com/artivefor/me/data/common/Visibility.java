package com.artivefor.me.data.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Visibility {
    PUBLIC("전체 공개"),
    PRIVATE("나만 보기");

    private final String description;
}