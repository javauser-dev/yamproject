package com.yam.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.admin.model.Admin; // ✅ 올바른 import

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin> findByIdAndPassword(String id, String password); // ✅ 추가

	Optional<Admin> findByIdEquals(String id);
}