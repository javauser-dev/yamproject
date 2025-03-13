package com.yam.store;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blacklisted_store")
public class BlacklistedStore {

	@Id
	private Long storeNo; // ✅ 사업자 ID (기존 Store 엔티티의 ID와 동일)

	@Column(nullable = false, length = 50)
	private String storeName; // ✅ 사업장 이름

	@Column(nullable = false, length = 30)
	private String storeBusinessNumber; // ✅ 사업자번호

	@Column(nullable = false, length = 50)
	private String storeEmail; // ✅ 사업자 이메일

	@Column(nullable = false, length = 30)
	private String storePhone; // ✅ 사업자 연락처

	@Column(nullable = false)
	private LocalDateTime bannedAt; // ✅ 불량 사업자 등록일

	@Column(nullable = false, length = 200)
	private String reason; // ✅ 불량 등록 사유
}
