package com.yam.store.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateDTO {
    private String oldEmail;         // 기존 이메일 (이메일 변경 시 필요)
    private String newEmail;         // 변경할 이메일
    private String verificationCode; // 이메일 인증 코드

    private String currentPassword;  // 현재 비밀번호 (비밀번호 변경 시 필요)
    private String newPassword;      // 변경할 비밀번호
    private String confirmPassword;  // 비밀번호 확인
}
