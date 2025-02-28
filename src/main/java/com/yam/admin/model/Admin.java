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
	private Long no;

	@Column(name = "id", nullable = false, unique = true, length = 20) // DB 컬럼명 일치 확인
	private String id;

	@Column(nullable = false, length = 20)
	private String password;
}
