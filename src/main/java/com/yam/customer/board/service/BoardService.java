package com.yam.customer.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yam.customer.board.domain.Board;
import com.yam.customer.board.repository.BoardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BoardService {
    
    private final BoardRepository boardRepository;

    // 카테고리별 게시글 목록 조회
    public List<Board> getBoardList(String category) {
        return boardRepository.findByBoardCategory(category);
    }

    // 특정 게시글 조회
    public Board getBoardDetail(Long boardNumber) {
        return boardRepository.findById(boardNumber).orElse(null);
    }
    
 // ✅ 전체 게시글 조회 메서드 추가
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

 // ✅ 조회수 증가
    public void increaseHit(Long boardNumber) {
        Board board = boardRepository.findById(boardNumber).orElse(null);
        if (board != null) {
            board.setLikeChar(board.getLikeChar() + 1); // ❌ 기존의 `likeChar`가 조회수로 잘못 사용됨
            boardRepository.save(board);
        }
    }

    // 게시글 저장 (등록, 수정)
    public void saveBoard(Board board) {
        if (board.getBoardNumber() == null) {
            board.setCreateBoard(LocalDateTime.now()); // 새 게시글 작성일 설정
        }
        board.setBoardUpdate(LocalDateTime.now()); // 항상 수정일 업데이트
        boardRepository.save(board);
    }
   

    @Transactional
    public int increaseLike(Long boardNumber) {
        Board board = boardRepository.findById(boardNumber).orElseThrow();
        board.setLikeChar(board.getLikeChar() + 1); // 좋아요 수 증가
        boardRepository.save(board);
        return board.getLikeChar(); // 업데이트된 좋아요 수 반환
    }
    
}
