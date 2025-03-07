package com.yam.admin.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "QNA")
public class Qna {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@SequenceGenerator(name = "qna_seq", sequenceName = "qna_seq", allocationSize = 1)
	private Long id;

	private String category;
	private String writer;
	private String title;
	private String content;

	@Column(name = "created_at", updatable = false)
	private LocalDate createdAt;

	private String answer;

	@Column(name = "answered_at")
	private LocalDate answeredAt;

	// 기본 생성자
	public Qna() {
	}

	// Getter & Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public LocalDate getAnsweredAt() {
		return answeredAt;
	}

	public void setAnsweredAt(LocalDate answeredAt) {
		this.answeredAt = answeredAt;
	}
}
