package com.yam.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yam.admin.model.Admin; // ✅ 올바른 import

import jakarta.transaction.Transactional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin> findByIdAndPassword(String id, String password); // ✅ 추가

	Optional<Admin> findByIdEquals(String id);

	Optional<Admin> findById(String id); // ✅ Long 타입 사용

	@Modifying
	@Transactional
	@Query("UPDATE Admin a SET a.profileImagePath = :profileImagePath WHERE a.id = :adminId")
	void updateProfileImage(@Param("adminId") String adminId, @Param("profileImagePath") String profileImagePath);

	// 전체 신규 회원 수
	@Query(value = "SELECT COUNT(*) FROM CUSTOMER_MANAGE", nativeQuery = true)
	int countNewUsers();

	// 전체 탈퇴 회원 수
	@Query(value = "SELECT COUNT(*) FROM WITHDRAWN_CUSTOMER", nativeQuery = true)
	int countDeletedUsers();
}
