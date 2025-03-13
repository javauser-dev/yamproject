package com.yam.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String customerId;
	private String customerPassword;
}