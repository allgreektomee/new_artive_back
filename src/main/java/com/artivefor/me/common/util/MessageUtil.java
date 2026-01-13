package com.artivefor.me.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private static MessageSource messageSource;

    // 생성자에 @Autowired를 명시하여 스프링이 확실히 주입하게 합니다.
    @Autowired
    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(MessageCode messageCode) {
        // 방어 코드 추가
        if (messageSource == null) {
            return messageCode.getCode(); // 주입 전이라면 키값이라도 반환
        }

        try {
            return messageSource.getMessage(
                    messageCode.getCode(),
                    null,
                    Locale.KOREAN // 또는 LocaleContextHolder.getLocale()
            );
        } catch (Exception e) {
            return messageCode.getCode(); // 메시지를 찾지 못하면 키값 반환
        }
    }
}