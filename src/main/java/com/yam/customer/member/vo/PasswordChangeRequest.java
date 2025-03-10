package com.yam.customer.member.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
 
@Data
public class PasswordChangeRequest {

	@NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
             message = "비밀번호는 영문자, 숫자, 특수문자를 각각 1개 이상 포함하여 8~15글자여야 합니다.")
    private String newPassword;
}