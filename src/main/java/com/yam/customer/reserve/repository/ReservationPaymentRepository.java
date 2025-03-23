package com.yam.customer.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.reserve.domain.ReservationPayment;
 
public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
	// customerReserveId로 ReservationPayment 조회
    ReservationPayment findByCustomerReserveId(Long customerReserveId);
}