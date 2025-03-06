package com.yam.admin.info.controller;

import java.util.List;

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

    @GetMapping("/members")
    public String memberList(Model model) {
        List<Member> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "admin/info/memberList"; // 뷰 이름 (templates/admin/info/memberList.html)
    }
    
    @GetMapping("/test")
    public String test() {
    	return "admin/info/test";
    }
}