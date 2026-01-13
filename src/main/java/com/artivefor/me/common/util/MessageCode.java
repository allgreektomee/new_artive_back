package com.artivefor.me.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageCode {


    //USER

    USER_PROFILE_UPDATE_SUCCESS("user.profile.update.success"),
    USER_NOT_FOUND("user.not.found"),
    PROFILE_GET_SUCCESS("user.profile.get.success"),
    //AUTH
    AUTH_SIGNUP_SUCCESS("auth.signup.success"),
    AUTH_EMAIL_SEND_SUCCESS("auth.email.send.success"),
    AUTH_EMAIL_VERIFY_SUCCESS("auth.email.verify.success"),
    AUTH_ALREADY_EXIST_EMAIL("auth.error.email.exists"), // 추가
    AUTH_EMAIL_VERIFY_FAIL("auth.error.email.verify.fail"), // 추가
    AUTH_LOGIN_SUCCESS("auth.login.success");

    private final String code;
}