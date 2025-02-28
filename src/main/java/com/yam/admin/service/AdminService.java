package com.yam.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.admin.dto.AdminDTO;
import com.yam.admin.repository.AdminRepository;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	public boolean login(String id, String password) {
		return adminRepository.findByIdAndPassword(id, password).isPresent();
	}

	public List<AdminDTO> getAdminDTO() {
		// TODO Auto-generated method stub
		return null;
	}
}
