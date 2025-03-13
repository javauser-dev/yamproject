package com.yam.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.admin.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
