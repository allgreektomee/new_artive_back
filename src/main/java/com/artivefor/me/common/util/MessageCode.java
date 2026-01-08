package com.artivefor.me.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageCode {


    //USER

    USER_PROFILE_UPDATE_SUCCESS("user.profile.update.success"),
    USER_NOT_FOUND("user.not.found"),

    //AUTH
    AUTH_SIGNUP_SUCCESS("auth.signup.success");

    private final String code;
}