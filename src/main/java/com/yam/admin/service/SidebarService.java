package com.yam.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.admin.repository.SidebarRepository;
import com.yam.customer.member.domain.Member;

@Service
public class SidebarService {

	@Autowired
	private SidebarRepository sidebarRepository;

	public List<Member> getAllCustomers() {
		return sidebarRepository.findAll();
	}
}
