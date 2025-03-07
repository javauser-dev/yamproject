package com.yam.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.store.Store;
import com.yam.store.dto.StoreDTO;
import com.yam.store.dto.StoreUpdateDTO;
import com.yam.store.email.service.EmailService2;
import com.yam.store.repository.StoreRepository;
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
    private EmailService2 emailService;

    // ✅ 사업자 회원가입 (이메일 인증 포함)
    @Transactional
    public void registerStore(StoreDTO storeDTO) {
        validateStore(storeDTO);  // 유효성 검사 호출

        // 이메일 중복 확인
        if (storeRepository.findByStoreEmail(storeDTO.getStoreEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화 후 Store 객체 생성
        Store store = Store.builder()
                .storeName(storeDTO.getStoreName())
                .storeNickname(storeDTO.getStoreNickname())
                .storeEmail(storeDTO.getStoreEmail())
                .storePassword(passwordEncoder.encode(storeDTO.getStorePassword()))  // ✅ 비밀번호 암호화
                .storePhone(storeDTO.getStorePhone())
                .storeBusinessNumber(storeDTO.getStoreBusinessNumber())
                .agree(storeDTO.isAgree())
                .enabled(false)  // 이메일 인증 완료 전까지 비활성화
                .verificationToken(UUID.randomUUID().toString())  // 인증 토큰 생성
                .build();

        // DB에 저장
        storeRepository.save(store);

        // 이메일 인증 메일 발송
        emailService.sendVerificationEmail(store.getStoreEmail());
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
        Store store = storeRepository.findByStoreEmail(email).orElseThrow(() -> new RuntimeException("사업자 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, store.getStorePassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        if (!store.isEnabled()) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        return jwtProvider.createToken(store.getStoreEmail(), "STORE");
    }

    public boolean updateStoreInfo(StoreUpdateDTO dto, String loggedInEmail) {
        // 로그인된 이메일을 기준으로 사업자 찾기
        Optional<Store> storeOptional = storeRepository.findByStoreEmail(loggedInEmail);

        if (storeOptional.isEmpty()) {
            return false; // 사업자를 찾을 수 없음
        }

        Store store = storeOptional.get();

        // ✅ 현재 비밀번호 검증 (필수 입력사항)
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해야 합니다.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), store.getStorePassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // ✅ 새 비밀번호 유효성 검사 (8자리 이상, 영어와 숫자 혼합)
        if (dto.getNewPassword() != null && !dto.getNewPassword().trim().isEmpty()) {
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 유효성 검사 (8자리 이상, 숫자와 영어 혼합)
            String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
            if (!dto.getNewPassword().matches(passwordRegex)) {
                throw new IllegalArgumentException("새 비밀번호는 8자리 이상이어야 하며, 영어와 숫자가 혼합되어야 합니다.");
            }

            store.setStorePassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        // ✅ 닉네임 변경 (유효성 검사 추가)
        if (dto.getNickname() != null && !dto.getNickname().trim().isEmpty()) {
            store.setStoreNickname(dto.getNickname());
        }

        // ✅ 전화번호 유효성 검사 (10자리에서 12자리 숫자만 허용)
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            String phoneRegex = "^\\d{10,12}$";
            if (!dto.getPhone().matches(phoneRegex)) {
                throw new IllegalArgumentException("전화번호는 10자리에서 12자리 사이의 숫자만 허용됩니다.");
            }
            store.setStorePhone(dto.getPhone());
        }

        storeRepository.save(store);
        return true; // 정보가 성공적으로 업데이트됨
    }
    
    public void removeStore(String storeEmail) {
        Optional<Store> storeOptional = storeRepository.findByStoreEmail(storeEmail);

        if (storeOptional.isPresent()) {
            Store store = storeOptional.get();
            storeRepository.delete(store);  // 사업자 삭제
        } else {
            throw new IllegalArgumentException("사업자를 찾을 수 없습니다.");
        }
    }


    // ✅ 유효성 검사 메서드
    private void validateStore(StoreDTO storeDTO) throws IllegalArgumentException {
        // 이메일 유효성 검사
        if (storeDTO.getStoreEmail() == null || !isValidEmail(storeDTO.getStoreEmail())) {
            throw new IllegalArgumentException("유효한 이메일을 입력해 주세요.");
        }

        // 사업자 번호 유효성 검사
        if (storeDTO.getStoreBusinessNumber() == null || !isValidBusinessNumber(storeDTO.getStoreBusinessNumber())) {
            throw new IllegalArgumentException("유효한 사업자 번호를 입력해 주세요.");
        }

        // 비밀번호 유효성 검사
        if (storeDTO.getStorePassword() == null || !isValidPassword(storeDTO.getStorePassword())) {
            throw new IllegalArgumentException("비밀번호는 8~20자여야 합니다.");
        }

        // 전화번호 유효성 검사
        if (storeDTO.getStorePhone() == null || !isValidPhone(storeDTO.getStorePhone())) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력 가능합니다.");
        }

        // 필수 필드 체크
        if (storeDTO.getStoreName() == null || storeDTO.getStoreName().isEmpty()) {
            throw new IllegalArgumentException("매장명을 입력해 주세요.");
        }

        if (storeDTO.getStoreNickname() == null || storeDTO.getStoreNickname().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해 주세요.");
        }

        // 약관 동의 체크
        if (!storeDTO.isAgree()) {
            throw new IllegalArgumentException("이용 약관에 동의해야 합니다.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailPattern);
    }

    private boolean isValidBusinessNumber(String businessNumber) {
        return businessNumber != null && businessNumber.length() == 10;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.length() <= 20;
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]+$");
    }

}
