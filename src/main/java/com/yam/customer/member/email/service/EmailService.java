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
		message.setText("인증 번호: " + verificationCode); // 내용

		javaMailSender.send(message);
	}
}
