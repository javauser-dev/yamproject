package com.yam.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateDTO {
	private String currentPassword; // 현재 비밀번호 (비밀번호 변경 시 필요)
	private String newPassword; // 변경할 비밀번호
	private String confirmPassword; // 비밀번호 확인
	private String nickname; // 변경할 닉네임
	private String phone; // 변경할 전화번호
}