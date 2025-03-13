package com.yam.menu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.menu.Menu;
import com.yam.menu.repository.MenuRepository;

import jakarta.transaction.Transactional;

@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	public List<Menu> getAllMenus() {
		return menuRepository.findAll();
	}

	public Menu getMenuById(Long id) {
		Optional<Menu> menuOpt = menuRepository.findById(id);
		return menuOpt.orElse(null);
	}

	@Transactional
	public Menu saveMenu(Menu menu) {
		return menuRepository.save(menu);
	}

	@Transactional
	public void deleteMenu(Long id) {
		menuRepository.deleteById(id);
	}

	public Menu getMenuForEdit(Long menuNo) {
		return menuRepository.findById(menuNo).orElseThrow(() -> new RuntimeException("Menu not found"));
	}

	public List<Menu> getMenusByCategory(String category) {
		return menuRepository.findByCategory(category);
	}
}