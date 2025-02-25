package com.yam.customer.member.vo;
import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequest {
    private String customerId;      // 변경
    private String customerPassword;  // 변경
    private String customerNickname;// 변경
    private String customerName;    // 변경
    private String customerEmail;   // 변경
    private LocalDate customerBirthDate; // 변경
   
    @Pattern(regexp = "[MF]", message = "성별은 M 또는 F 값만 허용됩니다.") // 정규 표현식으로 제한
    private String customerGender;
    private boolean agree; // customerApproval 필드와 매핑

    // customerProfileImage는 회원가입 시점에 받지 않는다고 가정
}