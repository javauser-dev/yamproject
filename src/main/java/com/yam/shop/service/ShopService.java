package com.yam.shop.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {
	
	
	private final ShopRepository shopRepository;
    private final Path rootLocation = Paths.get("uploads");

    public void saveFile(MultipartFile file) throws Exception {
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }
        Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
    }
    
    public Shop findById(Long id) {
        return shopRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 없습니다. ID: " + id));
    }
    
    public Shop getShop(Long shopNo) {

        return shopRepository.findById(shopNo).orElse(null); // shopNo가 정확한지 확인
    }

}