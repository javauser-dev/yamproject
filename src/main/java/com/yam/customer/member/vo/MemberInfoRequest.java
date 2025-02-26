package com.yam.customer.member.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberInfoRequest {

	@NotBlank(message = "비밀번호는 필수 입력값입니다.") // 비밀번호 필수
    @Pattern(regexp = "^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$", message = "비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.")
    private String customerPassword;
	
	// 닉네임 추가
    @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\\[\\]\\:;,.<>/?]{2,8}$", message = "닉네임은 영문 대소문자, 숫자, 특수문자, 한글 2~8글자여야 합니다.")
    private String customerNickname;

    // 이메일 필드는 더이상 사용하지 않음 (제거)

}