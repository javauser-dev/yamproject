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
@Table(name = "withdrawn_customer")  // 테이블 이름 지정
@EntityListeners(AuditingEntityListener.class)
public class WithdrawnMember {
	@Id
    private String customerId;

    @Column(nullable = false, length = 60)
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime customerCreateDate; // 가입일 필드 추가

    @Column(nullable = false)
    private LocalDateTime withdrawalRequestedAt; // 탈퇴 신청일

    @Column(nullable = false)
    private LocalDateTime withdrawalCompletedAt; // 탈퇴 완료일

    @Column(nullable = false)
    private String withdrawalReason; // 탈퇴 사유

}
