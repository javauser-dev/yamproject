package com.yam.shop.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;
import com.yam.shop.service.ShopService;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private ShopService shopService;

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
    

    @PostMapping("/addStore")
    public ResponseEntity<String> addStore(@RequestBody Map<String, Object> payload) {
        Shop shop = new Shop();
        shop.setShopName((String) payload.get("shopName"));
        shop.setStoreName((String) payload.get("storeName"));
        shop.setShopBusinessNumber((String) payload.get("shopBusinessNumber"));
        
        // LocalDate로 변환하여 설정
        String subDateStr = (String) payload.get("shopSubDate");
        LocalDate subDate = LocalDate.parse(subDateStr, DateTimeFormatter.ISO_DATE);
        shop.setShopSubDate(subDate);
        

        shop.setShopPhone((String) payload.get("shopPhone"));
        shop.setShopAddress((String) payload.get("shopAddress"));
        
        // 기본 카테고리 설정
        shop.setShopCategory("default"); // 필요 시 실제 카테고리 값 설정

        // 데이터베이스에 매장 정보 저장
        shopRepository.save(shop);

        return ResponseEntity.ok("매장이 성공적으로 추가되었습니다.");
    }
    
    @GetMapping("/shop/{id}")
    public String getShopDetail(@PathVariable Long id, Model model) {
        Shop shop = shopService.findById(id);
        if (shop == null) {
        	// shop이 null일 경우 처리
            return "errorPage";  // 오류 페이지로 리다이렉트 또는 보여주기
        }
        model.addAttribute("shop", shop);
        return "shop/myShop";
    }
    

    
    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            shopService.saveFile(file); // ShopService에서 파일 저장
            redirectAttributes.addFlashAttribute("message", "파일 업로드 성공");
            return "redirect:/shop/myShop"; // 업로드 후 매장 페이지로 리디렉션
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패");
            return "redirect:/shop/myShop";
        }
    }

}