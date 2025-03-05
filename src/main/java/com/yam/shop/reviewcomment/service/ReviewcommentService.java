package com.yam.shop.reviewcomment.service;

import java.util.List;

import com.yam.shop.reviewcomment.domain.Reviewcomment;

public interface ReviewcommentService {
	public List<Reviewcomment> commentList(Reviewcomment comment);
	public Reviewcomment commentInsert(Reviewcomment comment);
	public Reviewcomment commentUpdate(Reviewcomment comment);
	public void commentDelete(Reviewcomment comment);

}
