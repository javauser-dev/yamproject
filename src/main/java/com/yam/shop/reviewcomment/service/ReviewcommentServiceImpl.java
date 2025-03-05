package com.yam.shop.reviewcomment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yam.shop.reviewcomment.domain.Reviewcomment;
import com.yam.shop.reviewcomment.repository.ReviewcommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewcommentServiceImpl implements ReviewcommentService {
	
		private final ReviewcommentRepository reviewcommentRepository;
		
		@Override
		public List<Reviewcomment> commentList(Reviewcomment reviewcomment) { //아래 오류 리뷰엔티티명으로 바꿔 아마 no도 리뷰에 맞춰서 변경.
			List<Reviewcomment> commentList = reviewcommentRepository.reivewNoCommentList(reviewcomment.getReview().getNo());
			return commentList;
		}

		@Override
		public Reviewcomment commentInsert(Reviewcomment reviewcomment) {
			Reviewcomment result = reviewcommentRepository.save(reviewcomment); //이거 왜 오류나는건지 확인해.
			return result;
		}

		@Override
		public Reviewcomment commentUpdate(Reviewcomment reviewcomment) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void commentDelete(Reviewcomment reviewcomment) {
			// TODO Auto-generated method stub
			
		}

}
