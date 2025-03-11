package com.yam.admin.info.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "admin_customer_block") // 테이블 이름 변경
@EntityListeners(AuditingEntityListener.class)
public class AdminCustomerBlock {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_customer_block_seq") // 제너레이터 이름 변경
    @SequenceGenerator(name = "admin_customer_block_seq", sequenceName = "admin_customer_block_seq", allocationSize = 1) // 시퀀스 이름 변경
    @Column(name = "block_id")
    private Long blockId;

    @Column(name = "block_reason", nullable = false, length = 150)
    private String blockReason;

    @CreationTimestamp
    @Column(name = "block_createdate", nullable = false, updatable = false)
    private LocalDateTime blockCreateDate;

    @Column(name = "block_state", nullable = false)
    private int blockState; // 1: 차단, 2: 해제

    @Column(name = "customer_id", nullable = false) // 외래키, nullable=false 추가
    private String customerId; // FK.  String 타입

    @UpdateTimestamp
    @Column(name = "block_updatedate", nullable = false)
    private LocalDateTime blockUpdateDate;
}
