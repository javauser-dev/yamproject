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
	/*
    @Autowired
    private JavaMailSender mailSender;

    // ✅ ConcurrentHashMap 사용: 동시 접근 문제 방지
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();
    private final Map<String, String> verificationCodes = new HashMap<>();

    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString().substring(0, 6); // 6자리 랜덤 코드
        verificationCodes.put(email, token);
        System.out.println("📩 인증 코드 전송됨 → 이메일: " + email + " | 코드: " + token);
        // 실제 이메일 전송 로직 필요
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            verificationCodes.remove(email); // 인증 성공 후 코드 삭제
            return true;
        }
        return false;
    }
    // ✅ 인증 코드 전송
    public void sendVerificationEmail(String email, String token) {
        System.out.println("📨 이메일 전송 시도: " + email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + token);

        mailSender.send(message);
        System.out.println("✅ 이메일 전송 완료: " + email);

        // ✅ 인증 코드 저장
        verificationCodes.put(email, token);
        expirationTimes.put(email, System.currentTimeMillis() + (10 * 60 * 1000)); // 10분 후 만료
    }

    // ✅ 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        System.out.println("🧐 이메일 존재 여부 확인: " + verificationCodes.containsKey(email));

        if (!verificationCodes.containsKey(email)) {
            System.out.println("❌ 해당 이메일에 대한 인증 코드가 없음!");
            return false;
        }

        // ✅ 10분 만료 확인
        if (System.currentTimeMillis() > expirationTimes.get(email)) {
            System.out.println("⏳ 인증 코드 만료됨!");
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            return false;
        }
*/	
    @Autowired
    private JavaMailSender mailSender;
    
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();

    // ✅ 인증 코드 전송 메서드 (중복 제거)
    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString().substring(0, 6);
        verificationCodes.put(email, token);
        expirationTimes.put(email, System.currentTimeMillis() + (10 * 60 * 1000));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + token);
        mailSender.send(message);

        System.out.println("📩 인증 코드 전송 완료 → " + email + " | 코드: " + token);
    }

    // ✅ 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        if (!verificationCodes.containsKey(email)) return false;

        if (System.currentTimeMillis() > expirationTimes.get(email)) {
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            return false;
        }

        boolean isValid = verificationCodes.get(email).equals(code.trim());
        if (isValid) {
            verificationCodes.remove(email);
            expirationTimes.remove(email);
        }
        return isValid;
    }

    // ✅ 만료된 코드 정리 (10분마다 실행)
    @Scheduled(fixedRate = 600000)
    public void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        expirationTimes.entrySet().removeIf(entry -> now > entry.getValue());
        verificationCodes.keySet().removeIf(email -> !expirationTimes.containsKey(email));
        System.out.println("🗑️ 만료된 인증 코드 정리 완료!");
    }
    
    
    
}
