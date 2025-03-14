package com.yam.shop.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {

	private final ShopRepository shopRepository;

	private final Path rootLocation = Paths.get("static/upload/");

	public void saveFile(MultipartFile file) throws Exception {
		// 디렉토리 확인 후 생성
		Path rootLocation = Paths.get("static/upload");
		if (!Files.exists(rootLocation)) {
			Files.createDirectories(rootLocation);
		}

		// 파일을 지정된 경로에 저장
		Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
	}

	// 매장 정보 수정 처리 메소드 예시
	public void updateShop(Shop shop) {
		// 파일명이 없으면 기본 파일명(default.jpg) 설정
		if (shop.getFilename() == null || shop.getFilename().isEmpty()) {
			shop.setFilename("images/default.jpg"); // 기본 파일명 경로 수정
		}

		// 나머지 수정 로직
		shopRepository.save(shop);
	}

	public Shop findById(Long id) {
		return shopRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없습니다. ID: " + id));
	}

	public Shop getShop(Long shopNo) {

		return shopRepository.findById(shopNo).orElse(null); // shopNo가 정확한지 확인
	}

	public Shop getShopByStoreName(String storeName) {
		return shopRepository.findByStoreName(storeName); // storeName으로 매장 조회
	}

	// 매장 정보 저장
	public void save(Shop shop) {
		shopRepository.save(shop);
	}

	// 매장 정보 등록 처리 메소드 예시
	public void registerShop(Shop shop) {
		if (shop.getFilename() == null || shop.getFilename().isEmpty()) {
			shop.setFilename("images/default.jpg");
		}

		// 그 외의 등록 로직
		shopRepository.save(shop);
	}

	// 🔹 현재 로그인한 사업자의 이메일로 매장 조회
	public Shop findByStore_storeEmail(String storeEmail) {
		return shopRepository.findByStore_storeEmail(storeEmail).orElse(null);
	}

	public List<Shop> getAllShops() {
		return shopRepository.findAll(); // ShopRepository에서 모든 매장을 조회
	}

	// 매장 저장
	public void saveShop(Shop shop) {
		shopRepository.save(shop);
	}

//    public void deleteShop(Long id) {
//        shopRepository.deleteById(id);
//    }

	public Shop getShopById(Long id) {
		return shopRepository.findById(id).orElse(null); // ID로 샵 찾기, 없으면 null 반환
	}

	public Shop findShopByNo(Long shopNo) {
		return shopRepository.findByShopNo(shopNo).orElse(null); // shopNo로 매장 찾기
	}

	public boolean isBusinessNumberExists(String businessNumber) {
		return shopRepository.existsByShopBusinessNumber(businessNumber);
	}

	public Optional<Shop> findByShopNo(Long shopNo) {
		return shopRepository.findByShopNo(shopNo);
	}

	// 매장 삭제
	public void deleteShop(Long shopNo) {
		Optional<Shop> shop = shopRepository.findById(shopNo);
		if (shop.isPresent()) {
			shopRepository.delete(shop.get());
		} else {
			throw new RuntimeException("매장을 찾을 수 없습니다.");
		}
	}

}