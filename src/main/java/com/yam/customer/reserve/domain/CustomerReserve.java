package com.yam.customer.reserve.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import com.yam.customer.member.domain.Member;
import com.yam.shop.Shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_reserve")
@Getter
@Setter
@NoArgsConstructor //매개변수 없는 생성자
public class CustomerReserve {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_reserve_seq")
    @SequenceGenerator(name = "customer_reserve_seq", sequenceName = "customer_reserve_seq", allocationSize = 1)
    @Column(name = "cutomer_reserve_id")
    private Long id; // 예약 번호

    @Column(name = "cutomer_reserve_date", nullable = false)
    private LocalDate reserveDate; // 예약 날짜

    @Column(name = "cutomer_reserve_time", nullable = false) // columnDefinition 제거
    private LocalTime reserveTime;

    @Column(name = "cutomer_reserve_guest_count", nullable = false)
    private int guestCount; // 인원수

    @Column(name = "cutomer_reserve_deposit", nullable = false)
    private int deposit; // 예약금

    @Column(name = "cutomer_reserve_request", nullable = true)
    private String request; // 요청사항
    
    @ManyToOne(fetch = FetchType.LAZY)  //다대일 관계
    @JoinColumn(name = "shop_no", nullable = false)
    private Shop shop; // 매장 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 관계
    @JoinColumn(name = "customer_id", nullable = false)
    private Member member; // 회원 ID (FK)
    
    @Builder  //생성자에 @Builder를 넣으면 빌더 패턴을 통해 객체를 생성할 수 있다.
    public CustomerReserve(LocalDate reserveDate, LocalTime reserveTime,
                          int guestCount, int deposit, String request,
                          Shop shop, Member member) {
        this.reserveDate = reserveDate;
        this.reserveTime = reserveTime;
        this.guestCount = guestCount;
        this.deposit = deposit;
        this.request = request;
        this.shop = shop;
        this.member = member;
    }
}