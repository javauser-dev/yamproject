package com.yam.admin.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.admin.info.domain.AdminCustomerBlock;

public interface AdminBlockRepository extends JpaRepository<AdminCustomerBlock, Long> {
	// customerId와 blockState를 조건으로 최신 레코드 하나 조회
    AdminCustomerBlock findFirstByCustomerIdAndBlockStateOrderByBlockCreateDateDesc(String customerId, int blockState);
}
