package com.artivefor.me.data.common;

import lombok.Getter;

@Getter
public enum LanguageCode {
    KO("한국어"),
    EN("English"),
    JP("日本語"),
    CN("简体中文");

    private final String description;

    LanguageCode(String description) {
        this.description = description;
    }
}