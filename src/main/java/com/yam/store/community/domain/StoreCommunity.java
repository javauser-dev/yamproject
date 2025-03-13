package com.yam.store.community.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "store_community")
@SequenceGenerator(name = "store_community_generator", sequenceName = "store_community_seq", initialValue = 1, allocationSize = 1)
//자동으로 시퀀스를 적용한 거다.
public class StoreCommunity {
	/* 주석 사업자 게시판 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_community_generator")
	private Long storeListNo; //시퀀스 번호
	
	@Column(length = 20, nullable = false)
	private String storeCategory; //대분류

	@Column(length = 15, nullable = false)
	private String name; //사업자 이름

	@Column(length = 20, nullable = false)
	private String storeTitle; //제목

	@Lob
	private String storeContent; //내용

	@Column(length = 20, nullable = false)
	private String passwd; //비번

	/* 하이버네이트 */
	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	private LocalDateTime storeCreateAt; //등록일
	
	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	private LocalDateTime storeUpdateAt; //수정일

	@ColumnDefault(value = "0")
	private int storeHit; //조회수

	// @Transient: 필드를 맵핑하지 않을 때 사용
	@Transient
	private MultipartFile file;
	
	@Column
	@Builder.Default
	private String filename = ""; //실제 서버에 저장할 파일명
	
	//@Column
	//@Builder.Default
	//private String thumbname = "";//실제 서버에 저장할 썸네일 이미지 파일명

}
