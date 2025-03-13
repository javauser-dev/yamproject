package com.yam.customer.wishlist.wishlistentity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity  // 데이터베이스 테이블과 연결됨
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer_wishlist")  // 우리가 만든 테이블 이름과 같게 설정
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlist_seq_generator")
    @SequenceGenerator(name = "wishlist_seq_generator", sequenceName = "customer_wishlist_SEQ", allocationSize = 1)
    private Long wishId;  // 찜 ID

    @Column(nullable = false)
    private String customerId;  // 사용자 ID

    @Column(nullable = false)
    private Long shopNo;  // 음식점 ID

    @Column(length = 500)
    private String wishMemo;  // 메모 (사용자가 추가한 내용)

    private LocalDateTime wishCreatedAt = LocalDateTime.now();  // 생성 날짜
    
    @Column(nullable = true)
    private LocalDateTime wishUpdatedAt;
}
