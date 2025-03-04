package com.yam.customer.reserve.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            // customerReserveId는 아직 예약 정보가 생성되기 전이므로,
            // PaymentController에서는 null을 전달하는 것이 맞습니다.
            // (나중에 예약 정보와 연결하려면 별도의 로직이 필요합니다.)

            paymentService.savePayment(
                paymentDto.getPaymentAmount(),
                null, // customerReserveId는 null
                paymentDto.getCustomerId(),
                paymentDto.getShopId()
            );

            return new ResponseEntity<>("결제 정보 저장 성공", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("결제 정보 저장 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}