package com.yam.customer.reserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yam.customer.reserve.domain.CustomerReserve;

public interface CustomerReserveRepository extends JpaRepository<CustomerReserve, Long> {

	// 가장 최근 예약 ID를 찾는 쿼리 (수정)
    @Query("SELECT MAX(cr.id) FROM CustomerReserve cr")
    Optional<Long> findMaxReserveId();
}