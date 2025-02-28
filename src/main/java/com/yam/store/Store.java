package com.yam.store;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor 
@Table(name = "yam_store")
@SequenceGenerator(name = "yam_store_generator", sequenceName = "yam_store_seq", initialValue = 1, allocationSize = 1)
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yam_store_generator")
	private Long no;

	@Column(length = 30, unique = true, nullable = false) // ✅ 중복 방지
	private String businessNumber;

	@Column(length = 10, nullable = false)
	private String name;

	@Column(length = 100, nullable = false)
	private String password;

	@Column(length = 30, nullable = false)
	private String phone;

	@Column(length = 50, nullable = false, unique = true)
	private String email;

	@Column(length = 1, nullable = false)
	private boolean agree;

	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	private LocalDateTime subDate;

	@Transient
	private MultipartFile profile;

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

}