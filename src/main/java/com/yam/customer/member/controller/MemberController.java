package com.yam.customer.member.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.customer.member.domain.CustomUserDetails;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.email.service.EmailService;
import com.yam.customer.member.service.MemberService;
import com.yam.customer.member.vo.EmailVerificationRequest;
import com.yam.customer.member.vo.MemberSignupRequest; // DTO import
import com.yam.customer.member.vo.NicknameRequest;
import com.yam.customer.member.vo.PasswordChangeRequest;
import com.yam.customer.reserve.domain.CustomerReserve;
import com.yam.customer.reserve.repository.CustomerReserveRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class MemberController {

	@Autowired
	private final MemberService memberService;

	private final EmailService emailService;
	private final CustomerReserveRepository customerReserveRepository;

	@Value("${file.upload.path}")
	private String uploadPath; // 상대경로

	@GetMapping("/signup")
	public String showSignupForm(Model model) {
		model.addAttribute("memberSignupRequest", new MemberSignupRequest());
		return "customer/signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute("memberSignupRequest") @Valid MemberSignupRequest request,
			BindingResult bindingResult, Model model, HttpSession session) {

		if (bindingResult.hasErrors()) {
			return "customer/signup";
		}

		// 이메일 인증 여부 확인.
		Boolean verified = (Boolean) session.getAttribute("verified");
		if (verified == null || !verified) {
			model.addAttribute("errorMessage", "이메일 인증이 필요합니다.");
			return "customer/signup"; // 다시 signup 페이지로
		}

		// 인증된 이메일 주소와 입력한 이메일 주소가 같은지 확인
		String email = (String) session.getAttribute("verifiedEmail");
		if (!email.equals(request.getCustomerEmail())) {
			model.addAttribute("errorMessage", "인증받은 이메일 주소와 일치하지 않습니다.");
			return "customer/signup";
		}

		try {
			memberService.signup(request);
			session.removeAttribute("verified"); // 세션에서 인증 정보 삭제
			session.removeAttribute("verifiedEmail");
			// return "redirect:/customer/login";
			model.addAttribute("customerName", request.getCustomerName()); // 가입자 이름 추가.
			return "customer/signupSuccess"; // signupSuccess.html로 이동

		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "customer/signup";
		}
	}

	@GetMapping("/signup-success")
	public String signupSuccess() {
		return "customer/signupSuccess";
	}

	@GetMapping("/checkId")
	@ResponseBody
	public ResponseEntity<String> checkId(@RequestParam("customerId") String customerId) {
		boolean isDuplicated = memberService.isCustomerIdDuplicated(customerId);

		if (isDuplicated) {
			return ResponseEntity.ok("duplicated");
		} else {
			return ResponseEntity.ok("available");
		}
	}

	@GetMapping("/checkNickname")
	@ResponseBody
	public ResponseEntity<String> checkNickname(@RequestParam("customerNickname") String customerNickname,
			@RequestParam(value = "currentCustomerId", required = false) String currentCustomerId) {
		boolean isDuplicated = memberService.isCustomerNicknameDuplicated(customerNickname, currentCustomerId);

		if (isDuplicated) {
			return ResponseEntity.ok("duplicated");
		} else {
			return ResponseEntity.ok("available");
		}
	}

	// 인증 번호 발송
	@PostMapping("/sendVerificationCode")
	@ResponseBody
	public ResponseEntity<String> sendVerificationCode(@RequestParam("customerEmail") String customerEmail,
			HttpSession session) {
		// 인증 번호 생성 (6자리 숫자)
		Random random = new Random();
		int verificationCode = 100000 + random.nextInt(900000);

		// 이메일 발송
		try {
			emailService.sendVerificationEmail(customerEmail, String.valueOf(verificationCode));
			// 세션에 인증 번호 저장
			session.setAttribute("verificationCode", String.valueOf(verificationCode));
			session.setAttribute("verifiedEmail", customerEmail); // 인증메일을 보낸 이메일 주소도 저장.
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			e.printStackTrace(); // 로그에 에러를 남기는 것이 좋음.
			return ResponseEntity.ok("fail"); //
		}
	}

	// 인증 번호 확인
	@PostMapping("/verifyCode")
	@ResponseBody
	public ResponseEntity<String> verifyCode(@RequestParam("inputCode") String inputCode, HttpSession session) {
		String storedCode = (String) session.getAttribute("verificationCode");
		String email = (String) session.getAttribute("verifiedEmail");// 인증메일을 보낸 이메일 주소 불러옴

		if (storedCode != null && storedCode.equals(inputCode)) {
			// 인증 성공
			session.removeAttribute("verificationCode"); // 인증 성공했으므로 세션에서 제거.
			session.setAttribute("verified", true); // 인증 성공 여부를 세션에 저장

			return ResponseEntity.ok("verified");
		} else {
			// 인증 실패
			return ResponseEntity.ok("failed");
		}
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "customer/login"; // templates/customer/login.html
	}

	@GetMapping("/mypage")
	public String myPage(Model model, HttpSession session) {
		// ✅ 현재 인증된 사용자 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
			return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 이동
		}

		String customerId = (String) session.getAttribute("customerId");

		// ✅ principal이 UserDetails이면 ID 가져오기
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			customerId = userDetails.getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			// principal이 String인 경우 (일반적으로 익명 사용자)
			customerId = (String) authentication.getPrincipal();
		}

		if (customerId == null) {
			return "redirect:/login";
		}

		// ✅ 데이터베이스에서 회원 정보 조회
		Member member = memberService.getMemberById(customerId);
		if (member == null) {
			return "redirect:/login"; // 회원 정보를 찾을 수 없는 경우 로그인 페이지로 이동
		}

		// ✅ 프로필 이미지 설정
		String profileImageUrl = (member.getCustomerProfileImage() == null
				|| member.getCustomerProfileImage().isEmpty()) ? "/upload/customer_image_default.png"
						: member.getCustomerProfileImage();

		// ✅ 모델에 회원 정보 추가
		model.addAttribute("profileImageUrl", profileImageUrl);
		model.addAttribute("customerName", member.getCustomerName());
		// 모델에 데이터 추가
		model.addAttribute("customerNickname", member.getCustomerNickname()); // ✅ 닉네임 추가
		model.addAttribute("profileImageUrl", profileImageUrl);

		// ✅ 최근 예약 내역 추가
		Pageable topThree = PageRequest.of(0, 3);
		List<CustomerReserve> recentReserves = customerReserveRepository
				.findTop3ByMemberIdOrderByReserveDateDesc(customerId, topThree);
		model.addAttribute("recentReserves", recentReserves);

		return "customer/mypage";
	}

	@GetMapping("/memberInfo")
	public String showMemberInfo(HttpSession session, Model model) {
		// 🔹 1. 세션에서 로그인한 사용자 ID 가져오기
		String customerId = (String) session.getAttribute("customerId");

		if (customerId == null) {
			return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 이동
		}

		// 🔹 2. DB에서 회원 정보 조회
		Member member = memberService.getMemberById(customerId);

		if (member == null) { // 만약 회원 정보가 없다면 (예: 탈퇴한 회원)
			return "redirect:/login";
		}

		// 🔹 3. 프로필 이미지 설정 (기본 이미지 적용)
		String profileImageUrl = member.getCustomerProfileImage();
		if (profileImageUrl == null || profileImageUrl.isEmpty()) {
			profileImageUrl = "/upload/customer_image_default.png"; // 기본 이미지 경로
		}

		// 🔹 4. 모델에 데이터 추가
		model.addAttribute("profileImageUrl", profileImageUrl);
		model.addAttribute("member", member); // ✅ `member` 정보를 Thymeleaf에서 사용할 수 있도록 추가

		// 🔹 5. 회원 정보 페이지로 이동
		return "customer/memberInfo"; // templates/customer/memberInfo.html
	}

	@PostMapping("/updatePassword")
	public String updatePassword(@ModelAttribute("passwordChangeRequest") @Valid PasswordChangeRequest request, // DTO
																												// 사용
			BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails,
			RedirectAttributes redirectAttributes, Model model) {

		if (bindingResult.hasErrors()) {
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member);
			return "customer/memberInfo"; // 유효성 검사 실패 시
		}

		try {
			// 서비스 계층을 통해 비밀번호 변경
			memberService.updatePassword(customUserDetails.getMember().getCustomerId(), request.getNewPassword());
			redirectAttributes.addFlashAttribute("updateSuccess", true);
			return "redirect:/customer/mypage"; // 성공 시 마이페이지로

		} catch (Exception e) {
			// 예외 처리
			model.addAttribute("errorMessage", "비밀번호 변경 중 오류 발생: " + e.getMessage());
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member);
			return "customer/memberInfo";
		}
	}

	// 닉네임 변경
	@PostMapping("/updateNickname")
	public String updateNickname(@ModelAttribute("nicknameRequest") @Valid NicknameRequest request,
			BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails,
			RedirectAttributes redirectAttributes, Model model) {

		// @Valid 유효성 검증 결과 확인
		if (bindingResult.hasErrors()) {
			Member member = customUserDetails.getMember(); // 현재 회원 정보
			model.addAttribute("member", member); // 사용자가 입력한 값 + 기존 값 유지를 위해 모델에 다시 추가
			return "customer/memberInfo"; // 유효성 검사 실패 시, 다시 회원 정보 페이지로
		}

		try {
			memberService.updateNickname(customUserDetails.getMember().getCustomerId(), request.getCustomerNickname());
			redirectAttributes.addFlashAttribute("updateSuccess", true); // 성공 메시지 추가
			return "redirect:/customer/mypage"; // 성공 시 마이페이지로 리다이렉트

		} catch (Exception e) {
			// 예외 처리 (로그 기록, 사용자에게 친절한 에러 메시지 표시 등)
			model.addAttribute("errorMessage", "닉네임 변경 중 오류 발생: " + e.getMessage());
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member); // 사용자가 입력한 값 + 기존 값 유지를 위해 모델에 다시 추가
			return "customer/memberInfo"; // 실패 시 다시 회원 정보 페이지로
		}
	}

	// 이메일 변경 요청 처리
	@PostMapping("/updateEmail")
	public String updateEmail(@ModelAttribute("emailVerificationRequest") @Valid EmailVerificationRequest request,
			BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// Valid 어노테이션을 통해 들어온 값에 문제가 있으면, 다시 memberInfo페이지로 리다이렉트
		if (bindingResult.hasErrors()) {
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member);
			return "customer/memberInfo"; // 다시 회원 정보 페이지로
		}

		// 세션에서 인증 정보 확인 (이미 이메일 변경 시에만 이 메서드가 호출되므로, 세션 확인은 굳이 필요 없을 수 있음)
		Boolean verified = (Boolean) session.getAttribute("verified");
		if (verified == null || !verified) {
			model.addAttribute("errorMessage", "이메일 인증이 필요합니다.");
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member);
			return "customer/memberInfo";
		}

		String sessionEmail = (String) session.getAttribute("verifiedEmail");
		if (!request.getCustomerEmail().equals(sessionEmail)) {
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member);
			model.addAttribute("errorMessage", "인증된 이메일과 일치하지 않습니다.");
			return "customer/memberInfo";
		}

		try {
			// 이메일 업데이트
			memberService.updateEmail(customUserDetails.getMember().getCustomerId(), request.getCustomerEmail());

			// 세션에서 인증 정보 삭제
			session.removeAttribute("verified");
			session.removeAttribute("verifiedEmail");

			redirectAttributes.addFlashAttribute("updateSuccess", true);
			return "redirect:/customer/mypage";

		} catch (Exception e) {
			Member member = customUserDetails.getMember();
			model.addAttribute("member", member); // model에 member객체를 다시 넣어줌.
			model.addAttribute("errorMessage", "이메일 변경 중 오류 발생: " + e.getMessage());
			return "customer/memberInfo";
		}
	}

	@PostMapping("/updateProfileImage")
	public String updateProfileImage(@RequestParam("profileImage") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		// ✅ 현재 로그인한 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
			redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
			return "redirect:/login";
		}

		// ✅ 사용자 ID 가져오기
		String customerId = null;
		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetails) {
			customerId = ((UserDetails) principal).getUsername(); // 일반 로그인 사용자
		} else if (principal instanceof String) {
			customerId = (String) principal; // OAuth 로그인 사용자
		}

		if (customerId == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
			return "redirect:/customer/memberInfo";
		}

		Optional<Member> memberOptional = memberService.findById(customerId);
		if (memberOptional.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
			return "redirect:/customer/memberInfo";
		}

		Member member = memberOptional.get();

		// 4. 파일 저장 경로 설정 및 생성
		String uploadPath = "src/main/resources/static/upload"; // 상대 경로
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// 5. 파일 이름 생성 (중복 방지)
		String originalFilename = file.getOriginalFilename();
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

		// 6. 파일 저장
		Path savePath = Paths.get(uploadPath, uniqueFilename);
		try {
			Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
			return "redirect:/customer/memberInfo";
		}

		// 7. DB에 프로필 이미지 경로 업데이트 (상대경로 저장)
		String imageUrl = "/upload/" + uniqueFilename;
		member.setCustomerProfileImage(imageUrl); // 🔹 직접 Member 엔티티 업데이트
		memberService.save(member); // 🔹 변경된 내용 저장

		// ✅ 성공 메시지 추가
		redirectAttributes.addFlashAttribute("updateSuccess", "프로필 이미지가 성공적으로 변경되었습니다.");
		return "redirect:/customer/memberInfo"; // 🔹 같은 페이지 유지
	}

	@GetMapping("/withdrawalForm")
	public String showWithdrawalForm() {
		return "customer/withdrawalForm"; // withdrawalForm.html 뷰 반환
	}

	@PostMapping("/withdraw")
	@Transactional // 트랜잭션 처리
	public String withdraw(@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@RequestParam("withdrawalReason") String withdrawalReason, RedirectAttributes redirectAttributes,
			HttpSession session) {

		String customerId = (String) session.getAttribute("customerId");

		try {
			// withdrawn_customer 테이블로 이동
			memberService.moveToWithdrawn(customerId, withdrawalReason);

			// customer_manage 테이블에서 삭제
			memberService.deleteMember(customerId);

			// 세션 무효화 (로그아웃 처리)
			session.invalidate();

			redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
			return "redirect:/login"; // 로그인 페이지 또는 메인 페이지

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "회원 탈퇴 처리 중 오류가 발생했습니다: " + e.getMessage());
			return "redirect:/customer/memberInfo"; // 실패 시 회원 정보 페이지로
		}
	}
}