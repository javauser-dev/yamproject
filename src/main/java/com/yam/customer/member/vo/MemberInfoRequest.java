package com.yam.customer.member.vo;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberInfoRequest {

	// @NotBlank 제거
    @Pattern(regexp = "^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$", message = "비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.")
    private String customerPassword;

}