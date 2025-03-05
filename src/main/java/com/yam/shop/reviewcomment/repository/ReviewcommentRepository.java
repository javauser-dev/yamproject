package com.yam.shop.reviewcomment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yam.shop.reviewcomment.domain.Reviewcomment;

public interface ReviewcommentRepository extends JpaRepository<Reviewcomment, Long> {
	
		@Query("SELECT r FROM Reviewcomment r WHERE r.review.no=?1") //이것도 확인해봐야할듯
		List<Reviewcomment> ReviewNumberCommentList(Long reviewNumber);
		List<Reviewcomment> findByNickname(String nickname); //이거 아닐거임 바꿔.

}
