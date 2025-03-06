package com.yam.admin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.admin.model.Notice;
import com.yam.admin.model.NoticeComment;
import com.yam.admin.repository.NoticeCommentRepository;
import com.yam.admin.repository.NoticeRepository;

@Service
public class NoticeService {
	private final NoticeRepository noticeRepository;
	private final NoticeCommentRepository commentRepository;

	public NoticeService(NoticeRepository noticeRepository, NoticeCommentRepository commentRepository) {
		this.noticeRepository = noticeRepository;
		this.commentRepository = commentRepository;
	}

	// 공지사항 목록 조회
	public List<Notice> getAllNotices() {
		return noticeRepository.findAll();
	}

	// 공지사항 상세 조회
	public Optional<Notice> getNoticeById(Long id) {
		return noticeRepository.findById(id);
	}

	// 공지사항 작성 (관리자만 가능)
	public Notice createNotice(Notice notice) {
		return noticeRepository.save(notice);
	}

	// 공지사항 수정
	@Transactional
	public void updateNotice(Long id, Notice updatedNotice) {
		Optional<Notice> noticeOpt = noticeRepository.findById(id);
		if (noticeOpt.isPresent()) {
			Notice notice = noticeOpt.get();
			notice.setTitle(updatedNotice.getTitle());
			notice.setContent(updatedNotice.getContent());
			noticeRepository.save(notice);
		}
	}

	// 공지사항 삭제
	@Transactional
	public void deleteNotice(Long id) {
		noticeRepository.deleteById(id);
	}

	// 댓글 추가 (누구나 가능)
	public NoticeComment addComment(NoticeComment comment) {
		return commentRepository.save(comment);
	}
}
