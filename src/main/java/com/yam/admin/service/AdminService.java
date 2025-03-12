package com.yam.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.admin.dto.AdminDTO;
import com.yam.admin.model.Admin;
import com.yam.admin.repository.AdminRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	// ✅ 관리자 로그인 (비밀번호 암호화 없음)
	public boolean login(String id, String password) {
		return adminRepository.findByIdAndPassword(id, password).isPresent();
	}

	// ✅ 모든 관리자 가져오기 (DTO 변환)
	public List<AdminDTO> getAdminDTO() {
		return adminRepository.findAll().stream()
				.map(admin -> new AdminDTO(admin.getId(), admin.getName(), admin.getProfileImagePath()))
				.collect(Collectors.toList());
	}

	public AdminDTO getAdminById(String adminId) {
		Admin admin = adminRepository.findByIdEquals(adminId).orElse(null);

		if (admin == null) {
			System.out.println("❌ 관리자 정보 없음 (ID: " + adminId + ")");
			return new AdminDTO("admin", "관리자", "/images/default-profile.png"); // 기본 프로필 이미지 반환
		}

		String profileImage = (admin.getProfileImagePath() != null && !admin.getProfileImagePath().isEmpty())
				? admin.getProfileImagePath()
				: "/images/default-profile.png";

		return new AdminDTO(admin.getId(), admin.getName(), profileImage);
	}

	// ✅ 관리자 프로필 이미지 업데이트
	@Transactional
	public void updateAdminProfileImage(Long adminId, String profileImagePath) {
		Admin admin = adminRepository.findById(adminId).orElse(null);

		if (admin != null) {
			admin.setProfileImagePath(profileImagePath);
			adminRepository.save(admin); // ✅ 변경 사항 저장
			System.out.println("✅ DB에 저장된 프로필 이미지 경로: " + profileImagePath);
		} else {
			System.out.println("❌ 관리자 정보 없음 (ID: " + adminId + ")");
			throw new IllegalArgumentException("관리자를 찾을 수 없습니다.");
		}
	}

	public Map<String, Integer> getMemberStatistics() {
		Map<String, Integer> stats = new HashMap<>();

		// 🔹 회원 데이터 조회
		int newUsers = adminRepository.countNewUsers();
		int deletedUsers = adminRepository.countDeletedUsers();

		// 🔹 Map에 데이터 저장
		stats.put("newUsers", newUsers);
		stats.put("deletedUsers", deletedUsers);
		return stats;
	}

	// ✅ 사업자 통계 가져오기
	public Map<String, Integer> getStoreStatistics() {
		Map<String, Integer> storeStats = new HashMap<>();

		int newStores = adminRepository.countNewStores(); // 전체 사업자 수
		int deletedStores = adminRepository.countDeletedStores(); // 탈퇴한 사업자 수

		storeStats.put("newStores", newStores);
		storeStats.put("deletedStores", deletedStores);
		return storeStats;
	}

	// ✅ 관리자 ID (adminId) 기준으로 Admin 조회
	public Optional<Admin> findByAdminId(String adminId) {
		return adminRepository.findById(adminId);
	}

	// ✅ 관리자 정보 저장 (프로필 업데이트 시 필요)
	@Transactional
	public void save(Admin admin) {
		adminRepository.save(admin);
	}

}
