package com.yam.customer.reserve.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.reserve.domain.CustomerReserve;
import com.yam.customer.reserve.domain.Store;
import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.repository.StoreRepository;
import com.yam.customer.reserve.vo.ReserveRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional //선언적으로 트랜잭션 관리
public class ReserveService {

    private final CustomerReserveRepository customerReserveRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public Long createReserve(ReserveRequestDto requestDto) {
        // 1. 매장 정보 조회
        Store store = storeRepository.findById(requestDto.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + requestDto.getShopId()));

        // 2. 회원 정보 조회
        Member member = memberRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + requestDto.getCustomerId()));

        // 3. 예약 엔티티 생성 전에 request 값 null 체크 및 처리
        String request = requestDto.getRequest();
        if (request == null) {
            request = ""; // 또는 다른 기본값
        }

        // 4. 예약 엔티티 생성 (Builder 사용)
        CustomerReserve reserve = CustomerReserve.builder()
                .reserveDate(requestDto.getReserveDate())
                .reserveTime(requestDto.getReserveTime())
                .guestCount(requestDto.getGuestCount())
                .deposit(requestDto.getDeposit())
                .request(request) // null 처리된 request 값 사용
                .store(store)
                .member(member)
                .build();

        // 5. 예약 저장
        customerReserveRepository.save(reserve);

        // 6. 예약 id 반환
        return reserve.getId();
    }
}
