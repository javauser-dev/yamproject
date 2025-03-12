package com.yam.store.email.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailService2 {

	@Autowired
	private JavaMailSender mailSender;

	private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
	private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();

	// ✅ 인증 코드 전송 메서드 (중복 제거)
	public void sendVerificationEmail(String storeEmail) {
		String token = UUID.randomUUID().toString().substring(0, 6);
		verificationCodes.put(storeEmail, token);
		expirationTimes.put(storeEmail, System.currentTimeMillis() + (10 * 60 * 1000));

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(storeEmail);
		message.setSubject("이메일 인증 코드");
		message.setText("인증 코드: " + token);
		mailSender.send(message);

		System.out.println("📩 인증 코드 전송 완료 → " + storeEmail + " | 코드: " + token);
	}

	// ✅ 인증 코드 검증
	public boolean verifyCode(String storeEmail, String code) {
		if (!verificationCodes.containsKey(storeEmail))
			return false;

		if (System.currentTimeMillis() > expirationTimes.get(storeEmail)) {
			verificationCodes.remove(storeEmail);
			expirationTimes.remove(storeEmail);
			return false;
		}

		boolean isValid = verificationCodes.get(storeEmail).equals(code.trim());
		if (isValid) {
			verificationCodes.remove(storeEmail);
			expirationTimes.remove(storeEmail);
		}
		return isValid;
	}

	// ✅ 만료된 코드 정리 (10분마다 실행)
	@Scheduled(fixedRate = 600000)
	public void cleanExpiredCodes() {
		long now = System.currentTimeMillis();
		expirationTimes.entrySet().removeIf(entry -> now > entry.getValue());
		verificationCodes.keySet().removeIf(storeEmail -> !expirationTimes.containsKey(storeEmail));
		System.out.println("🗑️ 만료된 인증 코드 정리 완료!");
	}

}