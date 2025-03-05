package com.yam.shop.reviewcomment.controller;

import java.util.List;

//import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yam.shop.reviewcomment.domain.Reviewcomment;
import com.yam.shop.reviewcomment.service.ReviewcommentService;
import com.yam.store.article.domain.Article;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reviewcomments") //이거 부모쪽에서 바꿔줘.
@RequiredArgsConstructor
@Slf4j
public class ReviewcommentController { //이거는 그렇게 중요한게 아니니까 나중에 해. 리뷰코멘트라고만 바꿔주고 아티클을 회원쪽 리뷰라고 쓰면 될듯.
	private final ReviewcommentService reviewcommentService;
	
	@GetMapping(value="/all/{no}", produces=MediaType.APPLICATION_JSON_VALUE)
	public List<Reviewcomment> commentList(@PathVariable Long no, Reviewcomment reviewcomment, Article article) {
		article.setNo(no);
		//comment.setArticle(article); //여기에 연경이 리뷰 가져오면됨. 이름 바꿔.
		List<Reviewcomment> commentList = reviewcommentService.commentList(reviewcomment);
		return commentList;
	}
	
	@PostMapping(value="/commentInsert", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public Reviewcomment commentInsert(@RequestBody Reviewcomment reviewcomment) {
		//reviewcomment.getArticle().setNo(article.getNo());
		log.info(reviewcomment.toString());
		Reviewcomment result = reviewcommentService.commentInsert(reviewcomment);
		return result;
	}
	
	@PutMapping(value="/{id}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public Reviewcomment commentUpdate(@PathVariable Long id, @RequestBody Reviewcomment reviewcomment, Review review) {
		reviewcomment.setId(id);
		Reviewcomment result = reviewcommentService.commentUpdate(reviewcomment);
		return result;
	}
	
	@DeleteMapping(value = "/{id}", produces =  MediaType.TEXT_PLAIN_VALUE)
	public void commentDelete(@PathVariable Long id, Reviewcomment reviewcomment) {
		reviewcomment.setId(id);
		reviewcommentService.commentDelete(reviewcomment);  
	}
	
}


