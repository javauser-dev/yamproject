package com.yam.customer.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.member.vo.MemberSignupRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signup(MemberSignupRequest request) {
        if (memberRepository.existsByCustomerId(request.getCustomerId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String approvalStatus = request.isAgree() ? "Y" : "N";

        // Member 엔티티 생성 및 저장
        Member member = new Member(
                request.getCustomerId(),
                request.getCustomerPassword(), //  패스워드 암호화 필요 (BCryptPasswordEncoder 등 사용)
                request.getCustomerNickname(),
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerBirthDate(),
                request.getCustomerGender(),
                approvalStatus, // 약관동의 Y/N
                null            // 프로필 이미지는 나중에 처리
        );
        return memberRepository.save(member);
    }
}
