package com.yam.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.customer.member.domain.Member;

@Repository
public interface SidebarRepository extends JpaRepository<Member, String> {
}
