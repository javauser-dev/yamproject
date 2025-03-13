package com.yam.shop.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;
import com.yam.shop.service.ShopService;
import com.yam.store.Store;
import com.yam.store.service.StoreService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/store/shop")
@lombok.extern.slf4j.Slf4j
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private StoreService storeService;  // StoreService를 통해 로그인한 사업자 정보를 가져옴

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyBusiness(@RequestBody Map<String, String> data) {
        String businessNumber = data.get("shopBusinessNumber");
        String subDate = data.get("shopSubDate").replace("-", ""); // 'YYYYMMDD' 포맷으로 변환
        String bname = data.get("storeName");

        // 실제 API 호출 및 인증 로직 구현
        boolean isValid = verifyBusinessWithAPI(businessNumber, subDate, bname);

        Map<String, String> response = new HashMap<>();
        if (isValid) {
            response.put("message", "Verification successful!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Verification failed. Please try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private boolean verifyBusinessWithAPI(String businessNumber, String subDate, String bname) {
        try {
            // 국세청 API URL
            String serviceKey = "jEEUASAhUGj96H75IpTkbPCLORFLK3dhXoLFy9ApaJGIw65ZxLYGfSkcINdl3wy3gJ8XVQ64aKRHR1D6HGi6Cw==";
            String apiUrl = "https://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=" + serviceKey;

            // HTTP 요청 생성
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // JSON 요청 본문 작성
            String jsonInputString = "{\"businesses\": [{\"b_no\": \"" + businessNumber + "\", \"start_dt\": \"" + subDate + "\", \"p_nm\": \"" + bname + "\"}]}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // API 응답 분석 및 인증 상태 확인
                    return response.toString().contains("\"status_cd\":\"0000\""); // 상태 코드가 0000인 경우 인증 성공
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Create directory if it doesn't exist
                Path uploadPath = Paths.get("src/main/resources/static/upload/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename
                String filename = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());
                Path filePath = uploadPath.resolve(filename);
                
                // Save file
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Update shop entity
                Shop shop = shopService.getShopByStoreName("내 매장");
                if (shop != null) {
                    shop.setFilename(filename);
                    shopService.save(shop);
                }

                return "redirect:/store/shop/myShop";
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        return "redirect:/store/shop/myShop";
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
    
    @PostMapping("/register")
    public String registerShop(@ModelAttribute Shop shop) {
        if (shop.getFilename() == null || shop.getFilename().isEmpty()) {
            shop.setFilename("default.jpg"); // 기본값 설정
        }

        shopService.registerShop(shop);

        return "redirect:/store/shop/list";
    }

    // 매장 추가 처리 (POST 요청)
    @PostMapping("/addShop")
    public ResponseEntity<String> addShop(@RequestBody Shop shop, HttpSession session){
    	log.info("-----------------------------------------");
    	log.info(shop.toString());
    	try {
    		 Store store = (Store) session.getAttribute("loggedInStore");
    		 shop.setStore(store);
    		 shopService.saveShop(shop);
            
            return ResponseEntity.status(HttpStatus.CREATED).body("매장이 성공적으로 추가되었습니다.");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("매장 추가 중 오류가 발생했습니다.");
        }
    }
    
}	