package com.yam.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "admin")
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_seq")
	@SequenceGenerator(name = "admin_seq", sequenceName = "admin_seq", allocationSize = 1)
	private Long no; // ✅ PK 이름 유지

	@Column(name = "id", nullable = false, unique = true, length = 20)
	private String id;

	@Column(nullable = false, length = 20)
	private String password;

	@Column(nullable = false, length = 50) // ✅ 관리자 이름 필드 추가
	private String name;

	@Column(nullable = true) // 이미지가 없을 수도 있 음
	private String profileImagePath;

	// ✅ profileImagePath가 NULL이면 기본 이미지 반환
	public String getProfileImagePath() {
		return (profileImagePath != null && !profileImagePath.isEmpty()) ? profileImagePath
				: "/images/default-profile.png";
	}
}