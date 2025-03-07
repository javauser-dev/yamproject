package com.yam.customer.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.board.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // ✅ 특정 게시글 조회 ->친절한 서건이 작성

	List<Board> findByBoardCategory(String boardCategory);

    // ✅ 전체 게시글 조회
    List<Board> findAll(); 
    
}
