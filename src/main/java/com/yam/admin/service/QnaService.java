package com.yam.admin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yam.admin.model.Qna;
import com.yam.admin.repository.QnaRepository;

@Service
public class QnaService {
	private final QnaRepository qnaRepository;

	public QnaService(QnaRepository qnaRepository) {
		this.qnaRepository = qnaRepository;
	}

	public List<Qna> getAllQna() {
		return qnaRepository.findAll();
	}

	public Optional<Qna> getQnaById(Long id) {
		return qnaRepository.findById(id);
	}

	public void createQna(Qna qna) {
		qnaRepository.save(qna);
	}

	public void updateQna(Qna qna) {
		qnaRepository.save(qna);
	}

	public void deleteQna(Long id) {
		qnaRepository.deleteById(id);
	}
}
