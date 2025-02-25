package com.yam.customer.member.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_manage") // 테이블 이름을 customer_manage로 변경
@EntityListeners(AuditingEntityListener.class) //추가. JPA auditing 활성화
public class Member {

    @Id
    private String customerId;

    @Column(nullable = false)
    private String customerPassword;

    @Column(nullable = false)
    private String customerNickname;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerBirthDate;

    @Column(nullable = false, length = 1)
    private String customerGender;
    
    @Column(nullable = false)
    private String customerApproval;
    
    @Column
    private String customerProfileImage;
    
    @CreatedDate //자동으로 날짜가 주입.
    @Column(updatable = false) // 업데이트(수정) 불가
    private LocalDateTime customerRegDate; // 가입일 필드 추가
}