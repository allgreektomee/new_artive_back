package com.artivefor.me.dto.auth;

import lombok.Getter;
import lombok.Setter;

public class AuthRequest {

    @Getter
    @Setter
    public static class EmailSend {
        private String email;
    }

    @Getter @Setter
    public static class EmailVerify {
        private String email;
        private String code;
    }

    @Getter @Setter
    public static class SignUp {
        private String email;
        private String password;
        private String nickname;
        private String preferredLanguage; // "ko" 또는 "en"
    }

    @Getter
    @Setter
    public static class Login {
        private String email;
        private String password;
    }
}
