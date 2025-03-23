package com.yam.customer.reserve.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.reserve.domain.CustomerReserve;
import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.repository.ReservationPaymentRepository;
import com.yam.customer.reserve.repository.ShopRepository;
import com.yam.customer.reserve.vo.ReserveRequestDto;
import com.yam.shop.Shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service
@RequiredArgsConstructor
@Transactional //선언적으로 트랜잭션 관리
@Slf4j
public class ReserveService {

    private final CustomerReserveRepository customerReserveRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;

    // 예약 생성 (별도 트랜잭션)
    @Transactional
    public Long createReserve(ReserveRequestDto requestDto, String customerId) {
        // 1. 매장 정보 조회
    	Shop shop = shopRepository.findById(requestDto.getShopNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + requestDto.getShopNo()));

        // 2. 회원 정보 조회
        Member member = memberRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + customerId));

        // 3. 예약 엔티티 생성 전에 request 값 null 체크 및 처리
        String request = requestDto.getRequest();
        if (request == null) {
            request = ""; // 또는 다른 기본값
        }

        // 4. 예약 엔티티 생성
        CustomerReserve reserve = CustomerReserve.builder()
                .reserveDate(requestDto.getReserveDate())
                .reserveTime(requestDto.getReserveTime())
                .guestCount(requestDto.getGuestCount())
                .deposit(requestDto.getDeposit())
                .request(request)
                //.store(store)
                .shop(shop)
                .member(member)
                .build();

        // 5. 예약 저장
        customerReserveRepository.save(reserve);

        // 6. 예약 id 반환
        return reserve.getId();
    }
    
    // 전체 예약 목록 가져오기
    /*@Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<CustomerReserve> getAllReservesByCustomerId(String customerId) {
        // CustomerReserveRepository를 사용하여 전체 예약 목록 조회
        // (Member 객체를 통해 조회해야 함)
    	 Member member = memberRepository.findById(customerId)
                 .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + customerId));

         // 수정된 부분: 취소되지 않은 예약만 가져오기
         return customerReserveRepository.findByMemberAndReserveCancelIsZero(member);
    }*/
    
    // 전체 예약 목록 가져오기
    /*@Transactional(readOnly = true)
    public Page<CustomerReserve> getAllReservesByCustomerId(String customerId, Pageable pageable) {
        Member member = memberRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + customerId));

        return customerReserveRepository.findByMemberAndReserveCancelIsZero(member, pageable);
    }*/
    
    // 전체 예약 목록 가져오기 (검색 기능 추가)
    @Transactional(readOnly = true)
    public Page<CustomerReserve> getAllReservesByCustomerId(String customerId, String shopName, LocalDate reserveDate, Pageable pageable) {
        Member member = memberRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + customerId));

        return customerReserveRepository.findByMemberAndReserveCancelIsZeroAndSearchParams(member, shopName, reserveDate, pageable);
    }
}
