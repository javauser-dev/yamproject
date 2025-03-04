package com.yam.customer.reserve.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.reserve.domain.ReservationPayment;
import com.yam.customer.reserve.repository.ReservationPaymentRepository;
import com.yam.customer.reserve.vo.PaymentRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationPaymentRepository reservationPaymentRepository;

    @Transactional // 메서드 레벨 트랜잭션
    public void savePayment(PaymentRequestDto paymentRequestDto) {
        // ReservationPayment 엔티티 생성 및 값 설정
        ReservationPayment payment = new ReservationPayment();
        payment.setPaymentAmount(paymentRequestDto.getPaymentAmount());
        payment.setCustomerId(paymentRequestDto.getCustomerId());
        payment.setShopId(paymentRequestDto.getShopId());
        payment.setCustomerReserveId(1L);  // 요구사항에 따라 1로 설정
        payment.setPaymentDate(java.time.LocalDateTime.now()); // 현재 시간
        payment.setRefundDate(null);  //요구사항에 따라
        payment.setRefundStatus(1);  //요구사항에 따라

        // DB에 저장
        reservationPaymentRepository.save(payment);
    }
}