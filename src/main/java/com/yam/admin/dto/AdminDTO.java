package com.yam.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // ✅ 필수: JPQL에서 사용할 생성자 추가
public class AdminDTO {
	private Long no;
	private String id;
	private String password;
}
