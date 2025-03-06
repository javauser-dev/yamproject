package com.yam.admin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.model.Notice;
import com.yam.admin.model.NoticeComment;
import com.yam.admin.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/noticeList")
public class NoticeController {
	private final NoticeService noticeService;

	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	// 공지사항 목록 페이지
	@GetMapping
	public String noticeList(Model model) {
		model.addAttribute("notices", noticeService.getAllNotices());
		return "noticeList";
	}

	@GetMapping("/{id}")
	public String getNoticeDetail(@PathVariable("id") Long noticeId, Model model) {
		Optional<Notice> noticeOpt = noticeService.getNoticeById(noticeId);

		if (noticeOpt.isEmpty()) {
			return "redirect:/noticeList"; // 존재하지 않는 ID일 경우 목록으로 이동
		}

		Notice notice = noticeOpt.get();

		// 🚀 **comments 데이터도 함께 가져옴**
		List<NoticeComment> comments = notice.getComments();

		model.addAttribute("notice", notice);
		model.addAttribute("comments", comments); // ✅ comments를 따로 모델에 추가
		return "noticeDetail"; // Thymeleaf 파일명
	}

	// 공지사항 작성 페이지 (관리자만 가능)
	@GetMapping("/create")
	public String createNoticeForm(HttpSession session, Model model) {
		if (!"ADMIN".equals(session.getAttribute("userRole"))) {
			return "redirect:/noticeList";
		}
		model.addAttribute("notice", new Notice());
		return "noticeCreate";
	}

	// 공지사항 저장 (관리자만 가능)
	@PostMapping("/create")
	public String createNotice(@ModelAttribute Notice notice, HttpSession session) {
		if (!"ADMIN".equals(session.getAttribute("userRole"))) {
			return "redirect:/noticeList";
		}
		notice.setAuthor((String) session.getAttribute("userId"));
		noticeService.createNotice(notice);
		return "redirect:/noticeList";
	}

	// 공지사항 수정 폼
	@GetMapping("/{id}/edit")
	public String editNotice(@PathVariable("id") Long noticeId, Model model, HttpSession session) {
		if (!"ADMIN".equals(session.getAttribute("userRole"))) {
			return "redirect:/noticeList";
		}
		Optional<Notice> notice = noticeService.getNoticeById(noticeId);
		if (notice.isEmpty()) {
			return "redirect:/noticeList";
		}
		model.addAttribute("notice", notice.get());
		return "noticeEdit";
	}

	// 공지사항 수정 처리
	@PostMapping("/{id}/edit")
	public String updateNotice(@PathVariable("id") Long noticeId, @ModelAttribute Notice updatedNotice,
			HttpSession session) {
		if (!"ADMIN".equals(session.getAttribute("userRole"))) {
			return "redirect:/noticeList";
		}
		noticeService.updateNotice(noticeId, updatedNotice);
		return "redirect:/noticeList";
	}

	// 공지사항 삭제
	@PostMapping("/{id}/delete")
	public String deleteNotice(@PathVariable("id") Long noticeId, HttpSession session) {
		if (!"ADMIN".equals(session.getAttribute("userRole"))) {
			return "redirect:/noticeList";
		}
		noticeService.deleteNotice(noticeId);
		return "redirect:/noticeList";
	}

	// 댓글 작성
	@PostMapping("/{id}/comment")
	public String addComment(@PathVariable Long id, @ModelAttribute NoticeComment comment, HttpSession session) {
		Optional<Notice> notice = noticeService.getNoticeById(id);
		if (notice.isPresent()) {
			comment.setNotice(notice.get());
			comment.setCommenter((String) session.getAttribute("customerId"));
			comment.setCustomerNickname((String) session.getAttribute("customerNickname"));
			noticeService.addComment(comment);
		}
		return "redirect:/noticeList/" + id;
	}
}
