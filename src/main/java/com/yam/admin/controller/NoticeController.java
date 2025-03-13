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

	// ---------------------------------------------------------------
	// 댓글 작성

	@PostMapping("/{id}/comment")
	public String addComment(@PathVariable Long id, @ModelAttribute NoticeComment comment, HttpSession session) {
		Optional<Notice> notice = noticeService.getNoticeById(id);
		if (notice.isPresent()) {
			comment.setNotice(notice.get());

			// ✅ 회원(Member)과 사업자(Store)의 로그인 상태 확인
			String customerId = (String) session.getAttribute("customerId");
			String storeId = (String) session.getAttribute("storeId");
			String commenterNickname = "알 수 없음"; // 기본값 설정

			if (customerId != null) {
				// ✅ 회원이 로그인한 경우
				comment.setCommenter(customerId);
				commenterNickname = (String) session.getAttribute("customerNickname");
			} else if (storeId != null) {
				// ✅ 사업자가 로그인한 경우
				comment.setCommenter(storeId);
				commenterNickname = (String) session.getAttribute("storeNickname");
			}

			// ✅ 댓글 작성자 닉네임 설정 (null 방지)
			if (commenterNickname == null || commenterNickname.isEmpty()) {
				commenterNickname = "알 수 없음";
			}
			comment.setCustomerNickname(commenterNickname);

			noticeService.addComment(comment);
		}
		return "redirect:/noticeList/" + id;
	}

	// ✅ 댓글 삭제
	@PostMapping("/{noticeId}/comment/{commentId}/delete")
	public String deleteComment(@PathVariable Long noticeId, @PathVariable Long commentId, HttpSession session) {
		Optional<NoticeComment> commentOpt = noticeService.getCommentById(commentId);

		if (commentOpt.isPresent()) {
			NoticeComment comment = commentOpt.get();

			// ✅ 댓글 작성자 정보 확인
			if (comment.getCommenter() == null) {
				System.out.println("🚨 댓글 작성자 정보가 null입니다. commentId: " + commentId);
				return "redirect:/noticeList/" + noticeId;
			}

			// ✅ 로그인한 사용자 확인
			String customerId = (String) session.getAttribute("customerId");
			String storeId = (String) session.getAttribute("storeId");

			// ✅ 댓글 작성자가 로그인한 사용자와 일치하는 경우에만 삭제 허용
			if (comment.getCommenter().equals(customerId) || comment.getCommenter().equals(storeId)) {
				noticeService.deleteComment(commentId);
				System.out.println("✅ 댓글이 삭제되었습니다. commentId: " + commentId);
			} else {
				System.out.println("🚨 삭제 권한이 없습니다! 작성자: " + comment.getCommenter() + ", 로그인 사용자: "
						+ (customerId != null ? customerId : storeId));
			}
		} else {
			System.out.println("🚨 해당 댓글을 찾을 수 없습니다. commentId: " + commentId);
		}

		return "redirect:/noticeList/" + noticeId;
	}
}
