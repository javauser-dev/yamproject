package com.yam.shop.reviewcomment.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * JPA(Java Persistence API)는 자바로 DB에 명령을 내리게 하는 도구로, 데이터를 객체 지향적으로 다루는 기술이다. 
 * -엔티티와 리파지터리의 개념
 * • 엔티티: DB 데이터를 담는 자바 객체로, 엔티티를 기반으로 테이블 생성
 * • 리파지터리: 엔티티를 관리하는 인터페이스로, 데이터 CRUD 등의 기능 제공
 */

@Setter // 각 필드 값을 설정할 수 있는 setter 메서드 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 getter 메서드 자동 생성
@ToString
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor  // 매개변수가 아예 없는 기본 생성자 자동 생성
@Entity // 해당 클래스가 엔티티임을 선언, 클래스 필드를 바탕으로 DB에 테이블 생성
@Table(name = "shop_reviewcomment")
@SequenceGenerator(name="reviewcomment_generator", 
				sequenceName= "shop_reviewcomment_seq", 
				initialValue = 1, allocationSize = 1)
public class Reviewcomment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviewcomment_generator")
	private Long id; //대표키
	
	@Column
	private String shopName;//매장명 nickname; 댓글 작성자
	
	@Column
	private String body; //받을 내용
	
	@CreationTimestamp
	@ColumnDefault(value="sysdate")
	private LocalDateTime cdate; //댓글 단 날짜.
	
	//논리적 테이블에는 없는데 혹시 모르니까 둠. 0305 14:20
//	@CreationTimestamp
//	@ColumnDefault(value="sysdate")
//	private LocalDateTime udate; //댓글 수정한 날짜.
		
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="no", nullable = false)
//	@JsonBackReference
//	private Review review; 
	
	//연경님 리뷰 파트 끝나면 연동하면 됨.
	//뷰단을 만들어야 하는데 이거는 회원의 리뷰밑에 다는건데
	//리뷰는 매장에 있음
	

}
