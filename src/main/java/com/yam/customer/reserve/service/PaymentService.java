package com.yam.customer.reserve.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.reserve.domain.ReservationPayment;
import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.repository.ReservationPaymentRepository;
import com.yam.customer.reserve.vo.PaymentRequestDto;

import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationPaymentRepository reservationPaymentRepository;
    private final CustomerReserveRepository customerReserveRepository; // 추가

    @Transactional // 메서드 레벨 트랜잭션
    public void savePayment(PaymentRequestDto paymentRequestDto) {
        // ReservationPayment 엔티티 생성 및 값 설정
        ReservationPayment payment = new ReservationPayment();
        payment.setPaymentAmount(paymentRequestDto.getPaymentAmount());
        payment.setCustomerId(paymentRequestDto.getCustomerId());
        payment.setShopNo(paymentRequestDto.getShopNo());
        
        //payment.setCustomerReserveId(1L);  // 요구사항에 따라 1로 설정
        
        // 수정: customer_reserve_id의 최댓값 + 1을 customerReserveId로 설정
        Long maxReserveId = customerReserveRepository.findMaxReserveId().orElse(0L); // 최댓값이 없으면 0
        payment.setCustomerReserveId(maxReserveId + 1);
        
        payment.setPaymentDate(java.time.LocalDateTime.now()); // 현재 시간
        payment.setRefundDate(null);  //요구사항에 따라
        payment.setRefundStatus(1);  //요구사항에 따라

        // DB에 저장
        reservationPaymentRepository.save(payment);
    }
    
    // 환불 처리 메서드 (트랜잭션 전파 설정 변경)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 또는 Propagation.REQUIRED
    public void processRefund(Long customerReserveId) {
        // 1. customerReserveId를 사용하여 ReservationPayment 조회
        ReservationPayment payment = reservationPaymentRepository.findByCustomerReserveId(customerReserveId);

        // 2. payment가 null인지 확인 (null이면 예외 발생 또는 처리)
        if (payment == null) {
            // 예: 로그를 남기고, 예외를 발생시킴
            //log.error("해당 예약 ID에 대한 결제 정보가 없습니다. customerReserveId: {}", customerReserveId); //Slf4j
            throw new IllegalArgumentException("해당 예약 ID에 대한 결제 정보가 없습니다. customerReserveId: " + customerReserveId);
        }

        // 3. 환불 상태 및 날짜 업데이트
        payment.setRefundStatus(2); // 2: 환불 완료
        payment.setRefundDate(LocalDateTime.now()); // 현재 날짜/시간

        // 4. 변경 사항 저장
        reservationPaymentRepository.save(payment); //이미 @Transactional이므로, 변경사항이 자동으로 저장됨.
    }
}