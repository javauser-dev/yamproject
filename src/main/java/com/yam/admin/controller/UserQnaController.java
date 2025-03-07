package com.yam.admin.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.model.Qna;
import com.yam.admin.repository.QnaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/userqna") // 🔥 /qna와 구분하기 위해 userqna 사용
public class UserQnaController {

	private final QnaRepository qnaRepository;

	public UserQnaController(QnaRepository qnaRepository) {
		this.qnaRepository = qnaRepository;
	}

	// ✅ QnA 목록 (사용자가 작성한 QnA만 조회)
	@GetMapping
	public String getUserQnaList(HttpSession session, Model model) {
		String writer = (String) session.getAttribute("customerId"); // ✅ customerId 사용

		if (writer == null) {
			return "redirect:/login"; // 🔥 비회원이면 로그인 페이지로 이동
		}

		List<Qna> qnaList = qnaRepository.findByWriter(writer);
		model.addAttribute("qnaList", qnaList);
		return "user/qna/qnaList"; // 회원용 QnA 목록 페이지
	}

	// ✅ QnA 작성 페이지
	@GetMapping("/create")
	public String createQnaForm(HttpSession session, Model model) {
		if (session.getAttribute("customerId") == null) {
			return "redirect:/login"; // 🔥 로그인 필요
		}

		model.addAttribute("qna", new Qna());
		return "user/qna/qnaCreate"; // 회원용 QnA 작성 페이지
	}

	// ✅ QnA 저장 (회원이 질문 등록)
	@PostMapping("/create")
	public String createQna(@ModelAttribute Qna qna, HttpSession session, Model model) {
		String writer = (String) session.getAttribute("customerId");

		if (writer == null) {
			return "redirect:/login";
		}

		// ✅ 필수 입력값 검증
		if (qna.getTitle() == null || qna.getTitle().trim().isEmpty() || qna.getContent() == null
				|| qna.getContent().trim().isEmpty()) {
			model.addAttribute("error", "제목과 내용을 입력하세요.");
			model.addAttribute("qna", qna);
			return "user/qna/qnaCreate";
		}

		qna.setWriter(writer);
		qna.setCreatedAt(LocalDate.now());
		qnaRepository.save(qna);
		return "redirect:/userqna";
	}

	// ✅ QnA 상세보기 (본인이 작성한 QnA만 볼 수 있음)
	@GetMapping("/{id}")
	public String qnaDetail(@PathVariable Long id, HttpSession session, Model model) {
		String writer = (String) session.getAttribute("customerId");

		if (writer == null) {
			return "redirect:/login";
		}

		Optional<Qna> qna = qnaRepository.findById(id);

		if (qna.isPresent() && writer.equals(qna.get().getWriter())) {
			model.addAttribute("qna", qna.get());
			return "user/qna/qnaDetail";
		}

		return "redirect:/userqna";
	}

	// ✅ QnA 삭제
	@PostMapping("/{id}/delete")
	public String deleteQna(@PathVariable Long id, HttpSession session) {
		String writer = (String) session.getAttribute("customerId");

		if (writer == null) {
			return "redirect:/login";
		}

		Optional<Qna> qna = qnaRepository.findById(id);

		if (qna.isPresent() && writer.equals(qna.get().getWriter())) {
			qnaRepository.deleteById(id);
		}

		return "redirect:/userqna";
	}
}
