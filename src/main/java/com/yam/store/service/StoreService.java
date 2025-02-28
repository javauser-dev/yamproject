/*
package com.yam.store.service;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.store.Store;
import com.yam.store.dto.StoreDTO;
import com.yam.store.email.controller.StoreRepository;

import jakarta.transaction.Transactional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화

    @Transactional
    public void registerStore(StoreDTO storeDTO) {
        validateStore(storeDTO); // 🔹 유효성 검사 추가

        // 🔹 중복 이메일 체크
        if (storeRepository.findByEmail(storeDTO.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        Store store = Store.builder()
            .name(storeDTO.getName())
            .email(storeDTO.getEmail())
            .password(passwordEncoder.encode(storeDTO.getPassword())) // 비밀번호 암호화
            .phone(storeDTO.getPhone())
            .businessNumber(storeDTO.getBusinessNumber())
            .agree(storeDTO.isAgree())
            .enabled(false) // 이메일 인증 전까지 비활성화
            .verificationToken(UUID.randomUUID().toString()) // 🔹 이메일 인증 토큰 생성
            .build();

        storeRepository.save(store);

        // 🔹 이메일 인증 메일 발송
        sendVerificationEmail(store);
    }

    // ✅ 이메일 인증 처리 메서드
    public boolean verifyEmail(String token) {
        Store store = storeRepository.findByVerificationToken(token);

        if (store != null) {
            store.setEnabled(true); // 이메일 인증 성공 시 활성화
            store.setVerificationToken(null); // 인증 후 토큰 삭제
            storeRepository.save(store);
            return true;
        }
        return false;
    }

    // 🔹 이메일 발송 메서드 추가
    private void sendVerificationEmail(Store store) {
        String subject = "회원가입 이메일 인증";
        String verificationUrl = "http://localhost:8080/api/store/verify?token=" + store.getVerificationToken();
        String message = "이메일 인증을 완료하려면 다음 링크를 클릭하세요: " + verificationUrl;

        
    }

    // ✅ 유효성 검사 메서드
    private void validateStore(StoreDTO storeDTO) {
        if (!storeDTO.getName().matches("^[가-힣a-zA-Z]{2,10}$")) {
            throw new IllegalArgumentException("이름은 2~10자의 한글 또는 영문만 입력 가능합니다.");
        }
        if (!storeDTO.getPassword().matches("^.{8,20}$")) {
            throw new IllegalArgumentException("비밀번호는 8~20자 사이여야 합니다.");
        }
        if (!storeDTO.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
    }
}
*/

package com.yam.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.store.Store;
import com.yam.store.dto.StoreDTO;
import com.yam.store.dto.StoreUpdateDTO;
import com.yam.store.email.controller.StoreRepository;
import com.yam.store.email.service.EmailService;
import com.yam.store.security.JwtProvider;

import jakarta.transaction.Transactional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private EmailService emailService;

    // ✅ 사업자 회원가입 (이메일 인증 포함)
    @Transactional
    public void registerStore(StoreDTO storeDTO) {
        validateStore(storeDTO);

        if (storeRepository.findByEmail(storeDTO.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        Store store = Store.builder()
            .name(storeDTO.getName())
            .email(storeDTO.getEmail())
            .password(passwordEncoder.encode(storeDTO.getPassword())) // ✅ 비밀번호 암호화
            .phone(storeDTO.getPhone())
            .businessNumber(storeDTO.getBusinessNumber())
            .agree(storeDTO.isAgree())
            .enabled(false)
            .verificationToken(UUID.randomUUID().toString())
            .build();

        storeRepository.save(store);
        emailService.sendVerificationEmail(store.getEmail()); // ✅ 이메일 전송
    }

    // ✅ 이메일 인증 처리
    public boolean verifyEmail(String token) {
        Store store = storeRepository.findByVerificationToken(token);

        if (store != null) {
            store.setEnabled(true);
            store.setVerificationToken(null);
            storeRepository.save(store);
            return true;
        }
        return false;
    }

    // ✅ 로그인
    public String login(String email, String password) {
        Store store = storeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사업자 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, store.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        if (!store.isEnabled()) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        return jwtProvider.createToken(store.getEmail(), "STORE");
    }

    // ✅ 사업자 정보 업데이트
    public boolean updateStoreInfo(StoreUpdateDTO dto) {
        Optional<Store> storeOptional = storeRepository.findByEmail(dto.getOldEmail());

        if (storeOptional.isPresent()) {
            Store store = storeOptional.get();

            // ✅ 이메일 변경 (중복 검사 포함)
            if (!dto.getNewEmail().equals(dto.getOldEmail())) {
                if (storeRepository.existsByEmail(dto.getNewEmail())) {
                    return false;
                }
                store.setEmail(dto.getNewEmail());
                store.setEmailVerified(false);
            }

            // ✅ 비밀번호 암호화 후 저장
            if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
                store.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

            storeRepository.save(store);
            return true;
        }
        return false;
    }

    // ✅ 이메일 변경
    public void updateEmail(String oldEmail, String newEmail, String verificationCode) {
        Store store = storeRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new IllegalArgumentException("사업자를 찾을 수 없습니다."));

        if (!emailService.verifyCode(newEmail, verificationCode)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        if (storeRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        store.setEmail(newEmail);
        storeRepository.save(store);
    }

    // ✅ 비밀번호 변경
    public void updatePassword(String email, String currentPassword, String newPassword) {
        Store store = storeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사업자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, store.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")) {
            throw new IllegalArgumentException("비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다.");
        }

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        store.setPassword(passwordEncoder.encode(newPassword));
        storeRepository.save(store);
    }
    private void validateStore(StoreDTO storeDTO) {
        // 사업자명 검증 (비어 있으면 안 됨)
        if (storeDTO.getName() == null || storeDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("사업자명을 입력해야 합니다.");
        }
    }
    
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store findByBusinessNumber(String businessNumber) {
        return storeRepository.findByBusinessNumber(businessNumber)
                .orElse(null); // 없으면 null 반환
    }
    
    public Store findByEmail(String email) {
        return storeRepository.findByEmail(email).orElse(null);
    }
    @Transactional
    public void save(Store store) {
        storeRepository.save(store);
    }
    
    public Optional<Store> findByUsername(String username) {
        return storeRepository.findByUsername(username);
    }
}