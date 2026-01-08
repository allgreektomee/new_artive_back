package com.artivefor.me.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private static MessageSource staticMessageSource;

    public MessageUtil(MessageSource messageSource) {
        staticMessageSource = messageSource;
    }

    public static String getMessage(MessageCode messageCode) {
        if (staticMessageSource == null) {
            return "";
        }
        return staticMessageSource.getMessage(
                messageCode.getCode(),
                null,
                LocaleContextHolder.getLocale()
        );
    }

}