package com.yam.customer.member.controller;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yam.customer.member.email.service.EmailService;
import com.yam.customer.member.service.MemberService;
import com.yam.customer.member.vo.MemberSignupRequest; // DTO import

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService; // EmailService 주입

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest()); // DTO 사용
        return "customer/signup";
    }

    /*@PostMapping("/signup")
    public String signup(@ModelAttribute("memberSignupRequest") @Valid MemberSignupRequest request, // DTO 사용
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "customer/signup";
        }

        try {
            memberService.signup(request); // DTO를 서비스로 전달
            return "redirect:/customer/signup-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/signup";
        }
    }*/
    
    @PostMapping("/signup")
    public String signup(@ModelAttribute("memberSignupRequest") @Valid MemberSignupRequest request,
            BindingResult bindingResult,
            Model model, HttpSession session) { //HttpSession 추가

        if (bindingResult.hasErrors()) {
            return "customer/signup";
        }

        // 이메일 인증 여부 확인.
        Boolean verified = (Boolean) session.getAttribute("verified");
			if (verified == null || !verified) {
			    model.addAttribute("errorMessage", "이메일 인증이 필요합니다.");
			    return "customer/signup"; // 다시 signup 페이지로
			}

			//인증된 이메일 주소와 입력한 이메일 주소가 같은지 확인
			String email = (String)session.getAttribute("verifiedEmail");
			if(!email.equals(request.getCustomerEmail())) {
			    model.addAttribute("errorMessage", "인증받은 이메일 주소와 일치하지 않습니다.");
				return  "customer/signup";
			}


        try {
            memberService.signup(request);
            session.removeAttribute("verified"); //세션에서 인증 정보 삭제
            session.removeAttribute("verifiedEmail");
            return "redirect:/customer/signup-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/signup";
        }
    }

    
    @GetMapping("/checkId") // ID 중복검사
    @ResponseBody // AJAX 요청에 대한 응답으로 사용
    public ResponseEntity<String> checkId(@RequestParam("customerId") String customerId) {
        boolean isDuplicated = memberService.isCustomerIdDuplicated(customerId);

        if (isDuplicated) {
            return ResponseEntity.ok("duplicated"); // 중복됨
        } else {
            return ResponseEntity.ok("available"); // 사용 가능
        }
    }
    
    @GetMapping("/checkNickname") // 닉네임 중복검사
    @ResponseBody
    public ResponseEntity<String> checkNickname(@RequestParam("customerNickname") String customerNickname) {
        boolean isDuplicated = memberService.isCustomerNicknameDuplicated(customerNickname);

        if (isDuplicated) {
            return ResponseEntity.ok("duplicated"); // 중복
        } else {
            return ResponseEntity.ok("available"); // 사용 가능
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
            session.setAttribute("verifiedEmail", customerEmail); //인증메일을 보낸 이메일 주소도 저장.
            return ResponseEntity.ok("success");
        } catch (Exception e) {
             e.printStackTrace(); // 로그에 에러를 남기는 것이 좋음.
            return ResponseEntity.ok("fail"); //
        }
    }

     // 인증 번호 확인
	 @PostMapping("/verifyCode")
	 @ResponseBody
	 public ResponseEntity<String> verifyCode(@RequestParam("inputCode") String inputCode,
	                                          HttpSession session) {
	     String storedCode = (String) session.getAttribute("verificationCode");
	     String email = (String)session.getAttribute("verifiedEmail");//인증메일을 보낸 이메일 주소 불러옴
	
	     if (storedCode != null && storedCode.equals(inputCode)) {
	         // 인증 성공
	         session.removeAttribute("verificationCode"); // 인증 성공했으므로 세션에서 제거.
	         session.setAttribute("verified", true);       // 인증 성공 여부를 세션에 저장
	
	         return ResponseEntity.ok("verified");
	     } else {
	       // 인증 실패
	         return ResponseEntity.ok("failed");
	     }
	 }

    @GetMapping("/signup-success")
    public String signupSuccess() {
        return "customer/signupSuccess";
    }
}