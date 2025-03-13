package com.yam.store;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yam.shop.Shop;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "yam_store")
@SequenceGenerator(name = "yam_store_generator", sequenceName = "yam_store_seq", initialValue = 1, allocationSize = 1)
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yam_store_generator")
	private Long storeNo;

	@Column(length = 10, nullable = false)
	private String storeNickname;

	@Column(length = 30, unique = true, nullable = false) // ✅ 중복 방지
	private String storeBusinessNumber;

	@Column(length = 10, nullable = false)
	private String storeName;

	@Column(length = 100, nullable = false)
	private String storePassword;

	@Column(length = 30, nullable = false)
	private String storePhone;

	@Column(length = 50, nullable = false, unique = true)
	private String storeEmail;

	@Column(length = 1, nullable = false)
	private boolean agree;

	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate storeSubDate;

	@Transient
	private MultipartFile storeProfile;

	@Column
	@Builder.Default
	private String filename = "";

	// ✅ 이메일 인증 관련 필드
	@Column(nullable = false)
	private boolean enabled = false;

	@Column
	private String verificationToken;

	// ✅ **이메일 인증 여부**
	@Column(nullable = false)
	private boolean emailVerified = false;

	// ✅ **사업자번호 인증 여부**
	@Column(nullable = false)
	private boolean businessVerified = false;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)

	private List<Shop> shops;
	@Column(nullable = true, length = 20)
	private String storeRole = "ROLE_STORE"; // 🔹 기본값 "ROLE_STORE" 추가

}