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
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;
	private final MemberService memberService;

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

	// ✅ 게시글 작성 폼 (닉네임 자동 입력)
	@GetMapping("/freeboardform")
	public String boardForm(HttpSession session, Model model) {
		// 🔹 세션에서 로그인한 사용자 정보 가져오기
		String customerId = (String) session.getAttribute("customerId");

		if (customerId == null) {
			return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
		}

		// 🔹 Member 정보 조회
		Member member = memberService.getMemberById(customerId);
		if (member == null) {
			return "redirect:/login"; // 회원 정보가 없으면 로그인 페이지로 이동
		}

		model.addAttribute("nickname", member.getCustomerNickname()); // 닉네임 추가
		model.addAttribute("board", new Board()); // 새로운 게시글 객체 추가
		return "board/freeboardform";
	}

	// ✅ 게시글 등록 처리 (작성자 닉네임 자동 저장)
	@PostMapping("/create")
	public String createBoard(HttpSession session, @ModelAttribute Board board) {
		// 🔹 세션에서 로그인한 사용자 ID 가져오기
		String customerId = (String) session.getAttribute("customerId");

		if (customerId == null) {
			return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
		}

		// 🔹 Member 정보 조회
		Member member = memberService.getMemberById(customerId);
		if (member == null) {
			return "redirect:/login";
		}

		board.setCustomerId(member.getCustomerNickname()); // 게시글에 작성자 닉네임 저장
		board.setLikeChar(0); // 좋아요 기본값 0
		boardService.saveBoard(board);

		return "redirect:/board/freeboardList"; // 게시글 목록 페이지로 이동
	}

	// ✅ 게시글 좋아요 증가 API (AJAX 요청용)
	@PostMapping("/like/{boardNumber}")
	@ResponseBody
	public int likeBoard(@PathVariable Long boardNumber) {
		return boardService.increaseLike(boardNumber);
	}
}
