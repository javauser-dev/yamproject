package com.yam.admin.info.service;

import org.springframework.stereotype.Service;

import com.yam.admin.info.domain.AdminCustomerBlock;
import com.yam.admin.info.repository.AdminBlockRepository;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.MemberRole;
import com.yam.customer.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBanService {
	private final MemberRepository memberRepository;
    private final AdminBlockRepository adminBlockRepository;

    // 회원 차단
    public void banCustomer(String customerId, String blockReason) {
        Member member = memberRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 회원을 찾을 수 없습니다."));
        member.setMemberRole(MemberRole.BAN_CUSTOMER); // BAN_CUSTOMER 권한 설정
         //saveAdminCustomerBlock 메서드를 제거하고, banCustomer메서드에서 AdminCustomerBlock 저장
        AdminCustomerBlock adminBlock = new AdminCustomerBlock();
        adminBlock.setCustomerId(customerId);
        adminBlock.setBlockReason(blockReason);
        adminBlock.setBlockState(1); // 차단 상태
        adminBlockRepository.save(adminBlock); //저장
    }

    // 회원 차단 해제
    public void unbanCustomer(String customerId, String unblockReason) { // 해제 사유 파라미터 추가
        Member member = memberRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 회원을 찾을 수 없습니다."));
        member.setMemberRole(null); // 권한 제거
        memberRepository.save(member);

        AdminCustomerBlock latestBlock = adminBlockRepository
                .findFirstByCustomerIdAndBlockStateOrderByBlockCreateDateDesc(customerId, 1);
        if (latestBlock != null) {
            latestBlock.setBlockState(2); // 차단 해제 상태
            latestBlock.setBlockReason(unblockReason); // 해제 사유 업데이트
            adminBlockRepository.save(latestBlock);
        }
    }
}