package com.yam.admin.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yam.admin.model.Qna;
import com.yam.admin.repository.QnaRepository;

@Controller
public class QnaController {

	private final QnaRepository qnaRepository;

	public QnaController(QnaRepository qnaRepository) {
		this.qnaRepository = qnaRepository;
	}

	@GetMapping("/qna")
	public String getQnaList(Model model) {
		List<Qna> qnaList = qnaRepository.findAll();
		model.addAttribute("qnaList", qnaList);
		return "qna-list";
	}

	// ✅ 답변 작성 페이지 매핑 (qna-answer.html을 찾을 수 있도록)
	@GetMapping("/qna-answer/{id}")
	public String showAnswerForm(@PathVariable Long id, Model model) {
		Optional<Qna> qna = qnaRepository.findById(id);
		if (qna.isPresent()) {
			model.addAttribute("qna", qna.get());
			return "qna-answer"; // 🔥 여기서 파일명을 정확히 맞춰야 함
		} else {
			return "redirect:/qna";
		}
	}

	// ✅ 답변 저장 기능
	@PostMapping("/qna-answer/{id}")
	public String saveAnswer(@PathVariable Long id, @RequestParam String answer) {
		Optional<Qna> qna = qnaRepository.findById(id);
		if (qna.isPresent()) {
			Qna qnaEntity = qna.get();
			qnaEntity.setAnswer(answer);
			qnaEntity.setAnsweredAt(LocalDate.now());
			qnaRepository.save(qnaEntity);
		}
		return "redirect:/qna";
	}

	// ✅ 답변 수정 페이지 (답변이 이미 있는 경우)
	@GetMapping("/qna-edit/{id}")
	public String editAnswerForm(@PathVariable Long id, Model model) {
		Optional<Qna> qna = qnaRepository.findById(id);
		if (qna.isPresent()) {
			model.addAttribute("qna", qna.get());
			return "qna-edit";
		} else {
			return "redirect:/qna";
		}
	}

	// ✅ 답변 수정 기능
	@PostMapping("/qna-edit/{id}")
	public String updateAnswer(@PathVariable Long id, @RequestParam String answer) {
		Optional<Qna> qna = qnaRepository.findById(id);
		if (qna.isPresent()) {
			Qna qnaEntity = qna.get();
			qnaEntity.setAnswer(answer);
			qnaEntity.setAnsweredAt(LocalDate.now());
			qnaRepository.save(qnaEntity);
		}
		return "redirect:/qna";
	}

	// ✅ 기존 답변만 삭제 (Q&A 항목을 삭제하지 않음)
	@PostMapping("/delete/{id}")
	public String deleteAnswer(@PathVariable Long id) {
		Optional<Qna> qna = qnaRepository.findById(id);
		if (qna.isPresent()) {
			Qna qnaEntity = qna.get();
			qnaEntity.setAnswer(null); // 답변 삭제
			qnaEntity.setAnsweredAt(null); // 답변 날짜 삭제
			qnaRepository.save(qnaEntity);
		}
		return "redirect:/qna"; // 🔥 삭제 후 목록으로 이동
	}
}
