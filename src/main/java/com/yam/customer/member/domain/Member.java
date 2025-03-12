package com.yam.customer.member.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_manage")
@EntityListeners(AuditingEntityListener.class)
public class Member {

	@Id
	private String customerId;

	@Column(nullable = false, length = 60)
	private String customerPassword;

	@Column(nullable = false)
	private String customerNickname;

	@Column(nullable = false)
	private String customerName;

	@Column(nullable = false)
	private String customerEmail;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate customerBirthDate;

	@Column(nullable = false, length = 1)
	private String customerGender;

	@Column(nullable = false)
	private String customerApproval;

	@Column
	private String customerProfileImage;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime customerCreateDate; // 가입일 필드 추가

	@Column(nullable = true, length = 20)
	private String customerRole = "ROLE_CUSTOMER"; // 🔹 기본값 "ROLE_CUSTOMER" 추가

	// ✅ 이메일 인증 여부 추가
	@Column(name = "customer_email_verified", nullable = true)
	private Boolean customerEmailVerified = false;
}