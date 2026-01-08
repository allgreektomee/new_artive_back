package com.artivefor.me.data.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


    @MappedSuperclass
    @Getter
    @Setter
    public abstract class BaseTranslation {
        private boolean isAutomated = false;
        private LocalDateTime translatedAt; // 번역 시점
        private String translationEngine;    // 예: "DeepL", "GPT-4"
    }

