package com.yam.customer.reserve.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yam.customer.member.domain.Member;
import com.yam.customer.reserve.domain.CustomerReserve;
 
public interface CustomerReserveRepository extends JpaRepository<CustomerReserve, Long> {

	// 가장 최근 예약 ID를 찾는 쿼리
    @Query("SELECT MAX(cr.id) FROM CustomerReserve cr")
    Optional<Long> findMaxReserveId();
    
    // 특정 회원의 최근 예약 목록 3개 가져오기 (예약 날짜 내림차순, 취소되지 않은 예약)
    @Query("SELECT cr FROM CustomerReserve cr WHERE cr.member.id = :customerId AND cr.reserveCancel = 0 ORDER BY cr.reserveDate DESC")
    List<CustomerReserve> findTop3ByMemberIdOrderByReserveDateDesc(@Param("customerId") String customerId, Pageable pageable);
    
    // 특정 회원의 예약 목록 조회 (취소되지 않은 예약, reserveCancel = 0)
    //@Query("SELECT cr FROM CustomerReserve cr WHERE cr.member = :member AND cr.reserveCancel = 0")
    //List<CustomerReserve> findByMemberAndReserveCancelIsZero(@Param("member") Member member);
    
    // 특정 회원의 예약 목록 조회 (취소되지 않은 예약, reserveCancel = 0, 예약 번호 내림차순)
    @Query("SELECT cr FROM CustomerReserve cr WHERE cr.member = :member AND cr.reserveCancel = 0 ORDER BY cr.id DESC") // id 내림차순 정렬 추가
    Page<CustomerReserve> findByMemberAndReserveCancelIsZero(@Param("member") Member member, Pageable pageable);
    
    // 특정 회원으로 예약 목록 가져오기
    //List<CustomerReserve> findByMember(Member member);

    // 특정 회원의 예약 목록 조회 (검색 기능 추가) - LIKE 절 수정
    @Query("SELECT cr FROM CustomerReserve cr WHERE cr.member = :member AND cr.reserveCancel = 0 " +
           "AND (:shopName IS NULL OR LOWER(cr.shop.shopName) LIKE LOWER(CONCAT('%', :shopName, '%'))) " +  // LOWER, CONCAT 사용
           "AND (:reserveDate IS NULL OR cr.reserveDate = :reserveDate) " +
           "ORDER BY cr.id DESC")
    Page<CustomerReserve> findByMemberAndReserveCancelIsZeroAndSearchParams(
            @Param("member") Member member,
            @Param("shopName") String shopName,  // @Param 이름 확인
            @Param("reserveDate") LocalDate reserveDate,
            Pageable pageable);
}