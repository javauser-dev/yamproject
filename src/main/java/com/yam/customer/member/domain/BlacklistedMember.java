package com.yam.customer.member.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "blacklisted_member") // 불량 사용자 테이블
public class BlacklistedMember {

	@Id
	private String customerId; // ✅ 불량 사용자 ID

	@Column(nullable = false)
	private String customerName; // ✅ 회원 이름

	@Column(nullable = false)
	private String customerEmail; // ✅ 회원 이메일

	@Column(nullable = false)
	private LocalDateTime bannedAt; // ✅ 불량 사용자 등록 날짜

	@Column(nullable = true)
	private String reason; // ✅ 벤 사유 (선택 사항)
}
