package com.yam.store.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yam.store.Store;
import com.yam.store.dto.StoreDTO;
import com.yam.store.dto.StoreUpdateDTO;
import com.yam.store.service.StoreService;

@RestController
@RequestMapping("/store")
public class StoreController {

	@Autowired
    private StoreService storeService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerStore(@RequestBody StoreDTO storeDTO) {
        try {
            storeService.registerStore(storeDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "회원가입 성공"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "회원가입 실패: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "서버 오류 발생"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = storeService.verifyEmail(token);
        return verified ? ResponseEntity.ok("이메일 인증 완료!") : ResponseEntity.badRequest().body("이메일 인증 실패!");
    }
/*
    @PostMapping("/update")
    public ResponseEntity<?> updateStore(@RequestBody StoreUpdateDTO request) {
        try {
            if (request.getNewEmail() != null && request.getVerificationCode() != null) {
                storeService.updateEmail(request.getOldEmail(), request.getNewEmail(), request.getVerificationCode());
                return ResponseEntity.ok("이메일이 변경되었습니다.");
            }

            if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                    return ResponseEntity.badRequest().body("새 비밀번호가 일치하지 않습니다.");
                }
                storeService.updatePassword(request.getOldEmail(), request.getCurrentPassword(), request.getNewPassword());
                return ResponseEntity.ok("비밀번호가 변경되었습니다.");
            }

            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
  */
    @PostMapping("/update")
    public ResponseEntity<?> updateStore(@RequestBody StoreUpdateDTO request) {
        try {
            // 이메일 변경
            if (request.getNewEmail() != null) {
                storeService.updateEmail(request.getOldEmail(), request.getNewEmail(), request.getVerificationCode());
                return ResponseEntity.ok("이메일이 변경되었습니다.");
            }

            // 비밀번호 변경
            if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                    return ResponseEntity.badRequest().body("새 비밀번호가 일치하지 않습니다.");
                }
                storeService.updatePassword(request.getOldEmail(), request.getCurrentPassword(), request.getNewPassword());
                return ResponseEntity.ok("비밀번호가 변경되었습니다.");
            }

            return ResponseEntity.badRequest().body("변경할 정보가 없습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/store/edit")
    public String updateEmail(@RequestParam("newEmail") String newEmail) {
        // 로그인 구현이 안 됐으므로 임시로 특정 사업자 정보 가져오기
        Store store = storeService.findByEmail("test@store.com"); // 테스트용 이메일 (나중에 로그인 구현 후 수정)

        if (store == null) {
            return "redirect:/error";
        }

        store.setEmail(newEmail);
        storeService.save(store);

        return "redirect:/store/edit?success";
    }
}