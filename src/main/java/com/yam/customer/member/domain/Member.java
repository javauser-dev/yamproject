package com.yam.customer.member.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private LocalDate customerBirthDate;

    @Column(nullable = false, length = 1)
    private String customerGender;
    
    @Column(nullable = false)
    private String customerApproval;
    
    @Column
    private String customerProfileImage;
}