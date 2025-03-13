package com.yam.shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	Shop findByStoreName(String storeName); // storeName으로 Shop 엔티티를 조회

	Optional<Shop> findByStore_storeEmail(String storeEmail);

	Optional<Shop> findByShopNo(Long shopNo);

	boolean existsByShopBusinessNumber(String shopBusinessNumber);

}