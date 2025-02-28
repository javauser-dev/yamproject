package com.yam.customer.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.member.vo.MemberSignupRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSignupRequest request) {
        // MemberSignupRequest -> Member 엔티티로 데이터 복사
        Member member = new Member();
        member.setCustomerId(request.getCustomerId());
        // 비밀번호 암호화
        member.setCustomerPassword(passwordEncoder.encode(request.getCustomerPassword()));
        member.setCustomerNickname(request.getCustomerNickname());
        member.setCustomerName(request.getCustomerName());
        member.setCustomerEmail(request.getCustomerEmail());
        member.setCustomerBirthDate(request.getCustomerBirthDate());
        member.setCustomerGender(request.getCustomerGender());
        member.setCustomerApproval("Y");

        memberRepository.save(member);
    }

     public boolean isCustomerIdDuplicated(String customerId) {
         try {
             return memberRepository.existsById(customerId);
         } catch (Exception e) {
             // 예외 처리 (로그 기록, 사용자 정의 예외 발생 등)
             System.err.println("ID 중복 검사 중 오류 발생: " + e.getMessage());
             throw new RuntimeException("ID 중복 검사 중 오류 발생", e); // 더 나은 방법
         }
     }

     public boolean isCustomerNicknameDuplicated(String customerNickname, String currentCustomerId) {
         Member member = memberRepository.findByCustomerNickname(customerNickname); // Optional 사용 안 함
         if (member != null) {
             // 닉네임이 이미 존재
             if(currentCustomerId != null) { // myInfo에서 닉네임 중복 검사
                 return !member.getCustomerId().equals(currentCustomerId); // 현재 사용자의 ID와 비교
             }
             else{// Signup에서 닉네임 중복 검사.
                 return true;
             }
         }
         return false; // 닉네임 사용 가능
     }
     
     public Member getMemberById(String customerId) {
         return memberRepository.findById(customerId).orElse(null);
     }
    
     public void updatePassword(String customerId, String newPassword) {
         Member member = memberRepository.findById(customerId)
                 .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

         // 비밀번호 암호화 (새 비밀번호가 비어있지 않은 경우)
         if (newPassword != null && !newPassword.isEmpty()) {
             member.setCustomerPassword(passwordEncoder.encode(newPassword));
         }
         // 변경된 내용은 트랜잭션 내에서 자동으로 저장됨 (save 호출 불필요)
     }
     
     public void updateNickname(String customerId, String newNickname) {
         Member member = memberRepository.findById(customerId)
                 .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

         member.setCustomerNickname(newNickname);
         // memberRepository.save(member); // @Transactional에 의해 자동 저장
     }
     
     public void updateEmail(String customerId, String newEmail) {
         Member member = memberRepository.findById(customerId)
             .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

         member.setCustomerEmail(newEmail);
     }
     
     public void updateProfileImage(String customerId, String imageUrl) {
         Member member = memberRepository.findById(customerId)
                 .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

         member.setCustomerProfileImage(imageUrl);
         // @Transactional에 의해 자동 저장
     }
}