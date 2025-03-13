package com.yam.auth.Controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.auth.dto.NaverUser;
import com.yam.auth.service.NaverService;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/customer") // 기본 경로
@RequiredArgsConstructor
public class SignupController {
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	private final NaverService naverService;

	@GetMapping("/register")
	public String signupPage(HttpSession session, Model model) {
		// ✅ 세션에서 `NaverUser` 정보 가져오기
		NaverUser naverUser = (NaverUser) session.getAttribute("naverUser");

		if (naverUser != null) {
			model.addAttribute("fromNaver", true);
			model.addAttribute("name", naverUser.getName());
			model.addAttribute("email", naverUser.getEmail());
			model.addAttribute("birthDate", naverUser.getBirthDate());
		} else {
			model.addAttribute("fromNaver", false);
		}

		return "customer/register"; // `register.html`로 이동
	}

	// ✅ 네이버 로그인 후 회원가입 처리 (기존 /customer/signup에서 /customer/register로 변경)
	@PostMapping("/register")
	public String customerSignup(@RequestParam String customerId, @RequestParam String customerPassword,
			@RequestParam String customerNickname, @RequestParam String customerName,
			@RequestParam String customerEmail, @RequestParam String customerBirthDate,
			@RequestParam String customerGender,
			@RequestParam(required = false, defaultValue = "false") boolean fromNaver,
			RedirectAttributes redirectAttributes) {

		if (memberService.findById(customerId).isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
			return "redirect:/customer/register"; // 뷰 경로 변경
		}

		Member newMember = new Member();
		newMember.setCustomerId(customerId);
		newMember.setCustomerPassword(passwordEncoder.encode(customerPassword));
		newMember.setCustomerNickname(customerNickname);
		newMember.setCustomerName(customerName);
		newMember.setCustomerEmail(customerEmail);
		newMember.setCustomerBirthDate(java.time.LocalDate.parse(customerBirthDate));
		newMember.setCustomerGender(customerGender);
		newMember.setCustomerRole("ROLE_CUSTOMER");
		newMember.setCustomerApproval("Y");

		if (fromNaver) {
			newMember.setCustomerEmailVerified(true); // ✅ 네이버 로그인으로 가입 시 이메일 인증 완료 처리
		} else {
			newMember.setCustomerEmailVerified(false); // ✅ 일반 회원가입 시 이메일 인증 필요
		}

		memberService.save(newMember);
		return "redirect:/customer/mypage";
	}

	@GetMapping("/naver-callback")
	public String naverLoginCallback(@RequestParam String accessToken, Model model, HttpSession session) {
		System.out.println("Received Access Token: " + accessToken);
		// ✅ 1. accessToken 값 확인
		if (accessToken == null || accessToken.isEmpty()) {
			model.addAttribute("errorMessage", "Access Token이 없습니다!");
			return "errorPage"; // 에러 페이지
		}
		System.out.println("✅ Access Token: " + accessToken); // 콘솔 로그 확인

		// ✅ 2. 네이버 API 호출하여 사용자 정보 가져오기
		NaverUser naverUser = naverService.getUserInfo(accessToken);

		// ✅ 3. naverUser 정보가 정상적으로 들어오는지 확인
		if (naverUser == null) {
			model.addAttribute("errorMessage", "네이버 로그인 정보를 불러오는데 실패했습니다.");
			return "errorPage"; // 오류 페이지로 이동
		}
		System.out.println("✅ 네이버 사용자 정보: " + naverUser.toString());

		// ✅ 4. 세션에 저장하여 회원가입 시 사용
		session.setAttribute("naverUser", naverUser);

		return "redirect:/customer/register";
	}

}
