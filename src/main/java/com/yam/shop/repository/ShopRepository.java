package com.yam.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	
}
