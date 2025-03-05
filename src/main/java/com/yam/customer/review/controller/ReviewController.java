package com.yam.customer.review.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.yam.customer.review.domain.Review;
import com.yam.customer.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 파일 업로드 경로 (application.properties의 file.upload.path 값)
    @Value("${file.upload.path}")
    private String uploadPath;

    // 리뷰 목록 (검색 및 정렬 기능 포함)
    @GetMapping("/list")
    public String list(@RequestParam(value="keyword", required=false) String keyword,
                       @RequestParam(value="searchType", required=false, defaultValue="word") String searchType,
                       @RequestParam(value="sort", required=false, defaultValue="latest") String sortOption,
                       Model model) {
        List<Review> reviewList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            reviewList = reviewService.searchReviews(keyword.trim(), searchType);
        } else {
            Sort sort;
            if ("rating".equalsIgnoreCase(sortOption)) {
                sort = Sort.by(Sort.Direction.DESC, "rating");
            } else if ("popularity".equalsIgnoreCase(sortOption)) {
                sort = Sort.by(Sort.Direction.DESC, "likeCount");
            } else { // 최신순: 작성일 기준
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
            reviewList = reviewService.getReviewList(sort);
        }
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("sortOption", sortOption);
        return "review/list";  // templates/review/list.html
    }

    // 리뷰 상세보기
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Review review = reviewService.getReview(id);
        if (review == null) {
            return "redirect:/review/list";
        }
        model.addAttribute("review", review);
        return "review/detail";  // templates/review/detail.html
    }

    // 리뷰 작성/수정 폼 (id가 있으면 수정, 없으면 신규 작성)
    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        if (id != null) {
            Review review = reviewService.getReview(id);
            model.addAttribute("review", review);
        } else {
            model.addAttribute("review", new Review());
        }
        return "review/form";  // templates/review/form.html
    }

    // 리뷰 등록
    @PostMapping("/create")
    public String create(@ModelAttribute Review review,
                         @RequestParam("uploadFile") MultipartFile file) {
        // 파일 업로드 처리: 파일이 있으면 업로드 후 파일명을 imageUrl에 저장
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String filePath = uploadPath + File.separator + originalFilename;
                file.transferTo(new File(filePath));
                review.setImageUrl(originalFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reviewService.createReview(review);
        return "redirect:/review/list";
    }

    // 리뷰 수정
    @PostMapping("/update")
    public String update(@ModelAttribute Review review,
                         @RequestParam("uploadFile") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String filePath = uploadPath + File.separator + originalFilename;
                file.transferTo(new File(filePath));
                review.setImageUrl(originalFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reviewService.updateReview(review);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/review/list";
    }

    // 리뷰 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/review/list";
    }
}