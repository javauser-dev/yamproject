package com.yam.customer.wishlist.wishlistrepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.customer.wishlist.wishlistentity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByCustomerId(String customerId);  // 특정 사용자 찜 목록 조회

    // 특정 사용자의 특정 매장 찜 여부 확인
    Optional<Wishlist> findByCustomerIdAndShopNo(String customerId, Long shopNo);

    // 찜 삭제
    void deleteByCustomerIdAndShopNo(String customerId, Long shopNo);
    
    // customerId로 찜 목록을 찾고, wishId로 내림차순 정렬 (최신 ID 순)
    List<Wishlist> findByCustomerIdOrderByWishIdDesc(String customerId, Pageable pageable); // 수정
}