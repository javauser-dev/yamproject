package com.yam.customer.reserve.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.reserve.domain.ReservationPayment;
import com.yam.customer.reserve.repository.ReservationPaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

   private final ReservationPaymentRepository reservationPaymentRepository;
   
   @Transactional
   public void savePayment(ReservationPayment payment) {
       reservationPaymentRepository.save(payment);
   }

}