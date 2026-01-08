package com.artivefor.me.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender; // build.gradle에 추가한 mail starter가 자동으로 주입해줍니다.

    // 1. 6자리 랜덤 인증 번호 생성
    public String createCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000); // 100000 ~ 999999
    }

    // 2. 실제 메일 발송 로직
    public void sendVerificationEmail(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("[Artive] 회원가입을 위한 이메일 인증 번호입니다.");

        // 메일 본문 내용 (HTML 형식)
        String content = " <div style='margin:20px;'>" +
                "<h1>안녕하세요, Artive입니다.</h1>" +
                "<br>" +
                "<p>아래 코드를 복사해 가입 화면에 입력해주세요.<p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana;'>" +
                "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>" +
                "<div style='font-size:130%'>" +
                "CODE : <strong>" + code + "</strong><div><br/> " +
                "</div>";

        helper.setText(content, true);
        mailSender.send(message);
    }
}