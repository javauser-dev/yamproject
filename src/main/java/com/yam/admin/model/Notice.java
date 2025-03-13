package com.yam.admin.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ADMIN_NOTICE")

public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_seq")
	@SequenceGenerator(name = "notice_seq", sequenceName = "notice_seq", allocationSize = 1)

	@Column(nullable = false)
	private Long noticeId;

	@Column(length = 20, nullable = false)
	private String title;

	@Column(columnDefinition = "varchar2(255)")
	private String content;

	private String author; // 작성자 (관리자만 가능)

	private LocalDateTime createdAt = LocalDateTime.now();

	// 🚀 **NoticeComment와 관계 추가**
	@OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NoticeComment> comments = new ArrayList<>();

}