package com.yam.customer.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.member.vo.MemberSignupRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

    public void signup(MemberSignupRequest request) {
        // MemberSignupRequest -> Member 엔티티로 데이터 복사 (ModelMapper 사용 가능)
        Member member = new Member();
        member.setCustomerId(request.getCustomerId());
        // 비밀번호 암호화
        member.setCustomerPassword(passwordEncoder.encode(request.getCustomerPassword()));
        member.setCustomerNickname(request.getCustomerNickname());
        member.setCustomerName(request.getCustomerName());
        member.setCustomerEmail(request.getCustomerEmail());
        member.setCustomerBirthDate(request.getCustomerBirthDate());
        member.setCustomerGender(request.getCustomerGender());
        // agree 필드 값에 따라 customerApproval 설정
        if (request.isAgree()) {  // 또는 request.getAgree()
            member.setCustomerApproval("Y"); // 동의하면 "Y"
        } else {
            member.setCustomerApproval("N"); // 동의하지 않으면 "N" (사실 이 경우는 회원가입이 되면 안됨)
        }

        memberRepository.save(member);
    }
    
    public boolean isCustomerIdDuplicated(String customerId) {
    	try {
    		return memberRepository.existsById(customerId); // ID 존재 여부 확인
    		
		} catch (Exception e) {
			// 예외 처리 (로그 기록, 사용자 정의 예외 발생 등)
            System.err.println("ID 중복 검사 중 오류 발생: " + e.getMessage());
            return true; // 또는 false;  상황에 맞게 처리 (true로 하면 중복된 것처럼 처리)
            //throw new CustomException("ID 중복 검사 중 오류 발생", e); // 더 나은 방법
		}
        
    }
    
    public boolean isCustomerNicknameDuplicated(String customerNickname) {
        return memberRepository.existsByCustomerNickname(customerNickname); // 닉네임 중복 확인
    }
}
