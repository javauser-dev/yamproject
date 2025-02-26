package com.yam.customer.member.vo;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberSignupRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{2,8}$", message = "아이디는 영문 대소문자, 숫자 2~8글자여야 합니다.")
    private String customerId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$", message = "비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.")
    private String customerPassword;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\\[\\]\\:;,.<>/?]{2,8}$", message = "닉네임은 영문 대소문자, 숫자, 특수문자, 한글 2~8글자여야 합니다.")
    private String customerNickname;


    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]{1,18}$", message = "이름은 영문 대소문자, 한글 1~18글자여야 합니다.")
    private String customerName;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String customerEmail;

    @NotNull(message = "생년월일은 필수 입력값입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerBirthDate;

	@NotBlank(message = "성별은 필수 입력값입니다.")
    private String customerGender;

    @AssertTrue(message="회원가입에 동의해야 합니다")
    private boolean agree;
}