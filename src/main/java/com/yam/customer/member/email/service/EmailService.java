package com.yam.customer.member.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("회원 가입 이메일 인증"); // 제목
        //message.setText("인증 번호: " + verificationCode);    // 내용
        message.setText("안녕하세요. \n얌YAM을 이용해주셔서 진심으로 감사드립니다. \n아래 인증번호를 입력하셔서 회원가입을 완료해주세요. \n인증번호 : "
        		+ verificationCode);

        javaMailSender.send(message);
    }
}
