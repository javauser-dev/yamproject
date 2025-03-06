package com.yam.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.admin.model.NoticeComment;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
}