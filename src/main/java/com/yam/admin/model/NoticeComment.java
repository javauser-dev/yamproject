package com.yam.admin.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ADMIN_COMMENT")
public class NoticeComment {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
	@SequenceGenerator(name = "comment_seq", sequenceName = "comment_seq", allocationSize = 1)
	private Long commentId;

	private String commenter; // 댓글 작성자

	private String customerNickname; // ⭐ 추가된 필드 (사용자 닉네임)

	@Column(columnDefinition = "varchar2(255)")
	private String content;

	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "notice_id") // ✅ noticeId → notice_id로 변경 (DB 컬럼명에 맞춰 수정)
	private Notice notice;
}
