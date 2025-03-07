package com.yam.customer.review.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.review.domain.Review;
import com.yam.customer.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Review createReview(Review review) {
        // 별점은 필수 입력이므로 프론트엔드에서 검증 후 전달
        return reviewRepository.save(review);
    }

    @Override
    public Review getReview(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public Review updateReview(Review review) throws Exception {
        Optional<Review> opt = reviewRepository.findById(review.getId());
        if (!opt.isPresent()) {
            throw new Exception("리뷰를 찾을 수 없습니다.");
        }
        Review existing = opt.get();
        // 작성 후 24시간 이내에만 내용과 별점 수정 가능
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(existing.getCreatedAt(), now);
        if (duration.toHours() >= 24) {
            // 24시간 경과 시 내용과 별점은 기존 값 유지
            review.setContent(existing.getContent());
            review.setRating(existing.getRating());
        }
        // 업데이트 가능한 필드들 갱신
        existing.setStoreName(review.getStoreName());
        existing.setContent(review.getContent());
        existing.setRating(review.getRating());
        // writer와 createdAt은 변경 불가
        existing.setImageUrl(review.getImageUrl());
        existing.setTags(review.getTags());
        existing.setPublic(review.isPublic());
        existing.setAuthor(review.getAuthor());
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> searchReviews(String keyword, String searchType) {
        if ("tag".equalsIgnoreCase(searchType)) {
            return reviewRepository.findByTagsContaining(keyword);
        } else {
            return reviewRepository.findByStoreNameContainingOrContentContaining(keyword, keyword);
        }
    }
}