package com.yam.store.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.store.Store;
import com.yam.store.dto.StoreDTO;
import com.yam.store.dto.StoreUpdateDTO;
import com.yam.store.repository.StoreRepository;
import com.yam.store.service.StoreService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/store")
public class StoreController {

	@Autowired
	private StoreService storeService;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> registerStore(@RequestBody StoreDTO storeDTO) {
		try {
			storeService.registerStore(storeDTO);
			return ResponseEntity.ok(Collections.singletonMap("message", "회원가입 성공"));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", "회원가입 실패: " + e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "서버 오류 발생"));
		}
	}

	@GetMapping("/verify")
	public ResponseEntity<String> verifyEmail(@RequestParam String token) {
		boolean verified = storeService.verifyEmail(token);
		return verified ? ResponseEntity.ok("이메일 인증 완료!") : ResponseEntity.badRequest().body("이메일 인증 실패!");
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateStore(@RequestBody StoreUpdateDTO request, HttpSession session) {
		// 로그인된 사업자 정보 얻기
		Store loggedInStore = (Store) session.getAttribute("loggedInStore");

		if (loggedInStore == null) {
			return ResponseEntity.badRequest().body(new ErrorResponse("로그인이 필요합니다."));
		}

		try {
			boolean isUpdated = storeService.updateStoreInfo(request, loggedInStore.getStoreEmail());

			if (isUpdated) {
				return ResponseEntity.ok(new SuccessResponse("정보가 성공적으로 업데이트되었습니다."));
			} else {
				return ResponseEntity.badRequest().body(new ErrorResponse("정보 업데이트 실패!"));
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
	}

	// 응답 DTO 클래스
	class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	class SuccessResponse {
		private String message;

		public SuccessResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	@PostMapping("/withdraw")
	@Transactional // 트랜잭션 처리
	public String withdraw(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("withdrawalReason") String withdrawalReason, RedirectAttributes redirectAttributes,
			HttpSession session) {

		String storeEmail = userDetails.getUsername();

		try {
			// withdrawn_customer 테이블로 이동
			storeService.moveToWithdrawn(storeEmail, withdrawalReason);

			// 세션 무효화 (로그아웃 처리)
			session.invalidate();

			redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
			return "redirect:/login"; // 로그인 페이지 또는 메인 페이지

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "회원 탈퇴 처리 중 오류가 발생했습니다: " + e.getMessage());
			return "redirect:/store/mypage"; // 실패 시 회원 정보 페이지로
		}
	}

	// Controller 메서드 예시
	@DeleteMapping("/remove")
	public ResponseEntity<?> removeStore(HttpSession session) {
		// 로그인된 사업자 정보 얻기
		Store loggedInStore = (Store) session.getAttribute("loggedInStore");

		if (loggedInStore == null) {
			return ResponseEntity.badRequest().body("로그인이 필요합니다.");
		}

		try {
			storeService.removeStore(loggedInStore.getStoreEmail()); // 탈퇴 처리 서비스 호출
			session.invalidate(); // 세션 무효화
			return ResponseEntity.ok("사업자 탈퇴가 완료되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("탈퇴 처리에 실패했습니다. 서버 상태를 확인해주세요.");
		}
	}

}