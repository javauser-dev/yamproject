package com.yam.customer.reserve.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yam.customer.reserve.domain.ReservationPayment;
import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.service.PaymentService;
import com.yam.customer.reserve.vo.PaymentRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CustomerReserveRepository customerReserveRepository;



    @PostMapping("/payment/save")
    public ResponseEntity<?> savePaymentInfo(@RequestBody PaymentRequestDto paymentDto) {
        try {
            // customerReserveId를 가져오는 로직 (가장 최근 예약 ID + 1)
            Long nextReserveId = customerReserveRepository.findMaxReserveId()
                    .map(id -> id + 1)
                    .orElse(1L); // 예약 기록이 없으면 1부터 시작

            // DTO에서 필요한 정보 추출, ReservationPayment 객체 생성
            ReservationPayment payment = new ReservationPayment(
                paymentDto.getPaymentAmount(),
                nextReserveId, // 다음 예약 ID
                paymentDto.getCustomerId(),
                paymentDto.getShopId()
            );

            paymentService.savePayment(payment); //결제 정보 저장
            return new ResponseEntity<>("결제 정보 저장 성공", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("결제 정보 저장 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}