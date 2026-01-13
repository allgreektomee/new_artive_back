package com.artivefor.me.common.exception;

import com.artivefor.me.common.util.MessageCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final MessageCode messageCode;

    public BusinessException(MessageCode messageCode) {
        super(messageCode.getCode()); // 부모 생성자에는 키값 전달
        this.messageCode = messageCode;
    }
}