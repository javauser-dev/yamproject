package com.yam.customer.reserve.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yam.customer.member.domain.Member;
import com.yam.customer.reserve.domain.CustomerReserve;

public interface CustomerReserveRepository extends JpaRepository<CustomerReserve, Long> {

	// 가장 최근 예약 ID를 찾는 쿼리 (수정)
    @Query("SELECT MAX(cr.id) FROM CustomerReserve cr")
    Optional<Long> findMaxReserveId();
    
    // 특정 회원의 최근 예약 목록 3개 가져오기 (예약 날짜 내림차순)
    @Query("SELECT cr FROM CustomerReserve cr WHERE cr.member.id = :customerId ORDER BY cr.reserveDate DESC")
    List<CustomerReserve> findTop3ByMemberIdOrderByReserveDateDesc(@Param("customerId") String customerId, org.springframework.data.domain.Pageable pageable);
    
    // 특정 회원으로 예약 목록 가져오기
    List<CustomerReserve> findByMember(Member member);
}