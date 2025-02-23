package com.yam.customer.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.yam.customer.member.service.MemberService;
import com.yam.customer.member.vo.MemberSignupRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest());
        return "customer/signup"; // 경로 수정: templates/customer/signup.html
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("memberSignupRequest") @Valid MemberSignupRequest request,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "customer/signup"; // 경로 수정: templates/customer/signup.html
        }

        try {
            memberService.signup(request);
            return "redirect:/signup-success"; // 성공 페이지
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/signup"; // 경로 수정: templates/customer/signup.html
        }
    }

    @GetMapping("/signup-success")
    public String signupSuccess(){
        return "customer/signupSuccess"; // 경로 수정: templates/customer/signupSuccess.html
    }
}