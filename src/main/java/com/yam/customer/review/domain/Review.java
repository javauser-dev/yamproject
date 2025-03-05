package com.yam.customer.review.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 리뷰 엔티티 클래스
 * 매장명, 리뷰 내용, 별점, 태그, 이미지 경로, 작성자, 작성일, 공개 여부, 추천 여부, 추가 작성자 정보를 포함합니다.
 */
@Entity
@Getter
@Setter
@ToString
public class Review {

    // Primary Key: 리뷰 ID, 자동 생성됨.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매장명 (Store Name): null 값이 될 수 없음.
    @Column(nullable = false)
    private String storeName;

    // 리뷰 내용 (Review Content): 최대 길이 1000자, null 값 불가.
    @Column(length = 1000, nullable = false)
    private String content;

    // 별점 (Rating): 정수형, null 값 불가.
    @Column(nullable = false)
    private int rating;

    // 태그 (Tags): 여러 태그를 콤마로 구분하여 저장할 수 있음.
    private String tags;

    // 이미지 경로 (Image URL): 리뷰와 관련된 이미지 파일의 경로.
    private String imageUrl;

    // 작성자 (Writer): 리뷰 작성자, 한 번 등록되면 수정 불가.
    @Column(nullable = false, updatable = false)
    private String writer;

    // 작성일 (Created At): 리뷰가 등록될 때 자동 설정되며, 이후 수정 불가.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 공개 여부 (isPublic): true이면 전체 공개, false이면 사장님 전용.
    private boolean isPublic;

    // 추천 여부 (isRecommended): true이면 추천, false이면 추천하지 않음.
    private boolean isRecommended;

    // 추가적인 작성자 정보 (Author): 필요에 따라 사용.
    private String author;

    @CreationTimestamp
    @ColumnDefault(value = "sysdate")
    private LocalDateTime regDate;
    
    
    /**
     * 추가적인 작성자(author)를 반환합니다.
     * @return author
     */
    public String getAuthor() {
        return author;
    }
}