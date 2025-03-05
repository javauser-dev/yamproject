package com.yam.customer.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 단어 검색: 매장명 또는 내용에 키워드 포함
    List<Review> findByStoreNameContainingOrContentContaining(String storeName, String content);

    // 태그 검색: 태그에 키워드 포함
    List<Review> findByTagContaining(String tag);
}