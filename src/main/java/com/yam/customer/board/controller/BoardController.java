package com.yam.customer.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yam.customer.board.domain.Board;
import com.yam.customer.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // ✅ 게시글 목록 조회 (카테고리 필터링 + 전체보기)
    @GetMapping("/freeboardList") 
    public String boardList(@RequestParam(defaultValue = "전체") String category, Model model) {
        List<Board> boardList;

        if ("전체".equals(category)) {
            boardList = boardService.getAllBoards(); // 모든 게시글 조회
        } else {
            boardList = boardService.getBoardList(category); // 카테고리별 조회
        }
        model.addAttribute("boardList", boardList);
        model.addAttribute("category", category);
        return "board/freeboardList";
    }

    // ✅ 게시글 상세 조회 (조회수 증가)
    @GetMapping("/freeboarddetail/{boardNumber}")
    public String boardDetail(@PathVariable Long boardNumber, Model model) {
        boardService.increaseHit(boardNumber); // 조회수 증가
        Board board = boardService.getBoardDetail(boardNumber);
        model.addAttribute("board", board);
        return "board/freeboarddetail";
    }

    // ✅ 게시글 작성 폼
    @GetMapping("/freeboardform")
    public String boardForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/freeboardform";
    } 

    // ✅ 게시글 등록 처리 (추천 기본값 0으로 설정)
    @PostMapping("/create") 
    public String createBoard(@ModelAttribute Board board) {
        board.setLikeChar(0); // 기본값 설정
        boardService.saveBoard(board);
        return "redirect:/board/freeboardList";
    }
    // ✅ 게시글 좋아요 증가 API (AJAX 요청용)
    @PostMapping("/like/{boardNumber}")
    @ResponseBody
    public int likeBoard(@PathVariable Long boardNumber) {
        int updatedLikes = boardService.increaseLike(boardNumber);
        return updatedLikes; // 업데이트된 좋아요 수 반환
    }
    
}
