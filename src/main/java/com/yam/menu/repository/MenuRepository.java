package com.yam.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.menu.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
	List<Menu> findByCategory(String category);
}