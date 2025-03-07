package com.yam.admin.info.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/info") // URL 매핑
public class AdminInforController {

    private final MemberService memberService;
    
    @GetMapping("/main")
    public String mainPage() {
        return "admin/info/main";
    }

    @GetMapping("/members")
    public String memberList(Model model,
    						@PageableDefault(size = 10, sort = "customerId", direction = Sort.Direction.ASC) Pageable pageable) { // Pageable 파라미터 추가
		Page<Member> members = memberService.findAllMembersSortById(pageable);
		model.addAttribute("members", members);
		return "admin/info/memberList";
    }
}