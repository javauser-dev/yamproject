package com.yam.customer.reserve.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation_payment")
@Getter
@Setter
@NoArgsConstructor
public class ReservationPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_payment_seq")
	@SequenceGenerator(name = "reservation_payment_seq", sequenceName = "reservation_payment_seq", allocationSize = 1)
	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "payment_amount", nullable = false)
	private int paymentAmount;

	@Column(name = "payment_date", nullable = false)
	private LocalDateTime paymentDate;

	@Column(name = "refund_status", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
	private int refundStatus; // 1: 환불 미처리, 2: 환불 처리

	@Column(name = "refund_date")
	private LocalDateTime refundDate;

	@Column(name = "customer_reserve_id", nullable = true) // 변경: nullable = true
	private Long customerReserveId; // CustomerReserve의 외래키

	@Column(name = "customer_id", nullable = false)
	private String customerId; // Member (customer_manage 테이블)의 외래키

	@Column(name = "shop_no", nullable = false)
	private Long shopNo; // Shop (yam_shop 테이블)의 외래키

	// 생성자
	public ReservationPayment(int paymentAmount, Long customerReserveId, String customerId, Long shopNo) {
		this.paymentAmount = paymentAmount;
		this.paymentDate = LocalDateTime.now(); // 현재 시간으로 설정
		this.refundStatus = 1; // 기본값 1 (환불 미처리)
		this.customerReserveId = customerReserveId;
		this.customerId = customerId;
		this.shopNo = shopNo;
	}
}
