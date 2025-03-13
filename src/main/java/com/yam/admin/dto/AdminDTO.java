package com.yam.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
	private String id;
	private String name;
	private String profileImagePath;

	// ✅ 기본 프로필 이미지 경로 설정
	public String getProfileImagePath() {
		return ("/upload/" == null ? "/images/default-profile.png" : profileImagePath);
	}
}