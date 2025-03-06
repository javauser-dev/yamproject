package com.yam.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.admin.model.Qna;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
	List<Qna> findByWriter(String writer);

}