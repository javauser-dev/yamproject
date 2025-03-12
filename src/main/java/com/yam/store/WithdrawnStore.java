package com.yam.store;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "withdrawn_store") 
@EntityListeners(AuditingEntityListener.class)
public class WithdrawnStore {

	public WithdrawnStore() {}

	@Id
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
	
    @Column(nullable = false)
    private LocalDateTime withdrawalRequestedAt; // 탈퇴 신청일

    @Column(nullable = false)
    private LocalDateTime withdrawalCompletedAt; // 탈퇴 완료일

    @Column(nullable = false)
    private String withdrawalReason; // 탈퇴 사유


}