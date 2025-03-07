package com.yam.customer.review.service;


/*import java.util.List; import java.util.Optional;
  import org.springframework.stereotype.Service; 
  import org.springframework.transaction.annotation.Transactional;
  
 import com.yam.customer.review.domain.Review; 
 import com.yam.customer.review.repository.ReviewRepository;
 import lombok.RequiredArgsConstructor;

 @Service
 @RequiredArgsConstructor
  @Transactional 
  public class ReviewService {
  
  private final ReviewRepository reviewRepository;
  
  public Review saveReview(Review review) { 
	  return reviewRepository.save(review); }
  
  public Optional<Review> getReviewById(Long id) { 
	  return reviewRepository.findById(id); }
  
  public List<Review> searchByStoreName(String storeName) {
	  return reviewRepository.findByStoreNameContaining(storeName); }
  
  public List<Review> searchByTag(String tag) {
	  return reviewRepository.findByTagsContaining(tag); }
  
  public void deleteReview(Long id) { reviewRepository.deleteById(id); } }



package com.yam.customer.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.review.domain.Review;
import com.yam.customer.review.dto.ReviewRequestDto;
import com.yam.customer.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 전체 리뷰 조회
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // 리뷰 등록
    @Transactional
    public void createReview(ReviewRequestDto dto) {
        Review review = Review.builder()
                .storeName(dto.getStoreName())
                .content(dto.getContent())
                .rating(dto.getRating())
                .tags(dto.getTags())
                .isPublic(dto.isPublic())
                .author(dto.getAuthor())
                .build();
        reviewRepository.save(review);
    }
}
*/


import java.util.List;

import com.yam.customer.review.domain.Review;


public interface ReviewService {

    // 리뷰 등록
    Review createReview(Review review);

    // 리뷰 상세 조회
    Review getReview(Long id);

    // 24시간 이후에는 리뷰 내용과 별점 수정 불가
    Review updateReview(Review review) throws Exception;

    // 리뷰 삭제
    void deleteReview(Long id);

    // 전체 리뷰 목록 조회 (정렬 조건 없이 전체 조회)
    List<Review> getReviewList();

    // 검색: searchType이 "tag"이면 태그 검색, 그 외는 단어(매장명, 내용) 검색
    List<Review> searchReviews(String keyword, String searchType);
}
