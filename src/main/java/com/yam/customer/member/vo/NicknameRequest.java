package com.yam.customer.member.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NicknameRequest {
	
	@NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\\[\\]\\:;,.<>/?]{2,8}$", message = "닉네임은 영문 대소문자, 숫자, 특수문자, 한글 2~8글자여야 합니다.")
    private String customerNickname;
}
