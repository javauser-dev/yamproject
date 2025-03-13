package com.yam.shop.reserve.domain;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter // 각 필드 값을 설정할 수 있는 setter 메서드 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 getter 메서드 자동 생성
@Builder
@ToString
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor  // 매개변수가 아예 없는 기본 생성자 자동 생성
@Entity // 해당 클래스가 엔티티임을 선언, 클래스 필드를 바탕으로 DB에 테이블 생성
@Table(name = "shop_reserve")
@SequenceGenerator(name="reserve_generator", 
				sequenceName= "shop_reserve_seq", 
				initialValue = 1, allocationSize = 1)
public class ShopReserve { //예약한거 보기
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reserve_generator")
	private Long shopReserveNo; //대표키 리뷰댓글 번호
	
	@Column(nullable = false)
	private String shopDate;  // 예약날짜 
//	private LocalDate shopDate;

	@Column(nullable = false)
	private String shopTime; // 예약시간 이거 재확인 바람
//	private LocalTime shopTime;
	
	@Column(length = 3, nullable = false)
	private int shopGuestCount; // 인원수

	/* @Column(length = 20, nullable = false) */
	@ColumnDefault(value = "0")
	private String shopReserveDeposit; // 예약금
	
	@Column
	private String shopReserveRequest; // 요청사항
	
	@Column
	private String customerId; // 회원 id
	
	@Column(nullable = false)
	private String shopId; // 매장 id
	
	@CreationTimestamp
	@ColumnDefault(value="sysdate")
	private LocalDateTime confirmDate; //예약확인날짜와시간
	
	//패턴추가
	@CreationTimestamp
	@ColumnDefault(value="sysdate")
	private LocalDateTime rejectDate; //예약거절날짜와시간
	
		
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="no", nullable = false)
//	@JsonBackReference
//	private Review review;
	

}
