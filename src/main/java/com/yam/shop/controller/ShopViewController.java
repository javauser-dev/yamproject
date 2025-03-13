package com.yam.shop.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;
import com.yam.shop.service.ShopService;
import com.yam.store.Store;
import com.yam.store.repository.StoreRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/shop")
public class ShopViewController {
    
	@Value("${file.upload.directory:upload}")
    private String uploadDirectory;
    
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ShopService shopService;
    
    private static final Logger logger = LoggerFactory.getLogger(ShopViewController.class);
    private final Path uploadDir = Paths.get("upload/");
    
    @GetMapping("/new")
    public String newShop(Model model) {
        model.addAttribute("shop", new Shop());
        return "shop/newShop";
    }
    
    
    @GetMapping("/myShopList")
    public String shopList(Model model) {
        List<Shop> shopList = shopService.getAllShops();
        System.out.println(shopList);
        model.addAttribute("shopList", shopList);
        return "shop/myShopList";
    }

    @PostMapping("/save")
    public String saveShop(Shop shop) throws IOException {
        // Handle image upload
        MultipartFile mainImage = shop.getShopMainImage();
        MultipartFile menuImage = shop.getShopMenuImage();
        
        // 파일 처리 로직: shopMainImage 파일을 처리
        if (mainImage != null && !mainImage.isEmpty()) {
            String originalFilename = mainImage.getOriginalFilename();
            System.out.println("원본 파일명: " + originalFilename);
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // Create directory if it doesn't exist
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("디렉토리 생성 여부: " + created);
            }
            
            // Save main image
            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            try {
                mainImage.transferTo(destFile);
                System.out.println("파일 저장 성공: " + destFile.getAbsolutePath());
                
                // Set filename in shop object
                shop.setFilename(newFilename);
                System.out.println("저장된 파일명: " + newFilename);
            } catch (IOException e) {
                System.err.println("파일 저장 실패: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } else {
            // 파일이 없으면 기본 파일명 설정
            shop.setFilename("images/default.jpg");
        }

        // 메뉴 이미지도 처리
        if (menuImage != null && !menuImage.isEmpty()) {
            String originalFilename = menuImage.getOriginalFilename();
            System.out.println("원본 메뉴 이미지 파일명: " + originalFilename);

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // Save menu image
            File destFile = new File(uploadDirectory + File.separator + newFilename);
            try {
                menuImage.transferTo(destFile);
                System.out.println("메뉴 이미지 파일 저장 성공: " + destFile.getAbsolutePath());
                
                // 저장된 메뉴 이미지 파일명 저장 또는 Shop에 다른 필드로 처리
                // 예를 들어 shop.setMenuImageFilename(newFilename);
            } catch (IOException e) {
                System.err.println("메뉴 이미지 파일 저장 실패: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }

        // 나머지 샵 정보 저장
        LocalDate now = LocalDate.now();
        shop.setShopSubDate(now);
        
        shopService.updateShop(shop);
        
        return "redirect:/shop/myShopList";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteShop(@PathVariable Long id) {
        Shop shop = shopService.getShopById(id);
        
        // Delete image file if exists
        if (shop != null && shop.getFilename() != null && !shop.getFilename().isEmpty()) {
            File imageFile = new File(uploadDirectory + File.separator + shop.getFilename());
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
        
        // Delete from database
        shopService.deleteShop(id);
        
        return "redirect:/shop/myShopList";
    }
    
    @GetMapping("/myShop")
    public String viewMyShop(@RequestParam(required = false) Long id, Model model, 
                            Principal principal, HttpSession session) {
        // 세션이 존재하는지 확인하고 없으면 생성
        String sessionId = session.getId();
        logger.info("현재 세션 ID: {}", sessionId);
        Shop shop;
        
        if (id != null) {
            shop = shopService.findShopByNo(id);
            if (shop == null) {
                return "redirect:/shop/myShopList";
            }
        } else {
            if (principal == null) {
                return "redirect:/login";
            }
            
            String email = principal.getName();
            Store store = storeRepository.findByStoreEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사업자를 찾을 수 없습니다."));
            
            shop = shopService.getShopByStoreName(store.getStoreName());
            
            if (shop == null) {
                shop = new Shop();
                shop.setStoreName(store.getStoreName());
            }
        }
        
        model.addAttribute("shop", shop);
        return "shop/myShop";
    }

    
    @GetMapping("/upload/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Path file = Paths.get("upload").resolve(filename);
        Resource resource = new FileSystemResource(file.toFile());
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/images/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getDefaultImage(@PathVariable String filename) {
        Path path = Paths.get("src/main/resources/static/images").resolve(filename).toAbsolutePath();
        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping("/shopDetail/{id}")
    public String showShopDetail(@PathVariable Long id, Model model) {
        System.out.println("showShopDetail called with id: " + id);
        Shop shop = shopService.findShopByNo(id);
        if (shop != null) {
            System.out.println("Shop found: " + shop);
            model.addAttribute("shop", shop);
            return "shop/shopDetail";
        } else {
            System.out.println("Shop not found for id: " + id);
            return "에러";
        }
    }
    
    @PostMapping("/update-image")
    @ResponseBody
    public ResponseEntity<?> updateImage(@RequestParam("shopMainImage") MultipartFile shopMainImage, 
                                        Principal principal) {
        try {
            if (shopMainImage.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "이미지를 선택해주세요."));
            }
            
            // Ensure directory exists
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate unique filename with UUID
            String originalFilename = shopMainImage.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String filename = UUID.randomUUID().toString() + extension;
            Path targetPath = Paths.get(directory.getAbsolutePath(), filename);
            
            // Save file
            Files.copy(shopMainImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update shop information
            if (principal != null) {
                String email = principal.getName();
                Store store = storeRepository.findByStoreEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사업자를 찾을 수 없습니다."));
                
                Shop shop = shopService.getShopByStoreName(store.getStoreName());
                if (shop != null) {
                    shop.setFilename(filename);
                    shopRepository.save(shop);
                }
            }
            
            return ResponseEntity.ok(Map.of("filename", filename));
        } catch (Exception e) {
            logger.error("이미지 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        }
        return ResponseEntity.ok().body("인증됨: " + principal.getName());
    }

    @PostMapping("/update-field")
    @ResponseBody
    public ResponseEntity<?> updateField(
    	@RequestParam(value = "field", required = false) String field,
        @RequestParam(value = "value", required = false) String value,
        @RequestParam(value = "shopNo", required = false) Long shopNo,
        Principal principal,
        HttpSession session) {

        System.out.println("Field: " + field);
        System.out.println("Value: " + value);
        System.out.println("ShopNo: " + shopNo);
        if (field == null || field.isEmpty() || value == null || value.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "필드와 값은 필수입니다."));
        }

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "사용자가 인증되지 않았습니다."));
        }

        try {
            String storeEmail = principal.getName();
            Shop shop;
            
            if (shopNo != null) {
                // ShopNo가 제공된 경우 해당 번호로 매장 조회
                Optional<Shop> optionalShop = shopService.findByShopNo(shopNo);
                if (optionalShop.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "매장을 찾을 수 없습니다."));
                }
                shop = optionalShop.get();
                
                // 현재 사용자가 이 매장의 소유자인지 확인
                if (!shop.getStore().getStoreEmail().equals(storeEmail)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "이 매장을 수정할 권한이 없습니다."));
                }
            } else {
                // 이전 방식: 이메일로 매장 조회
                shop = shopService.findByStore_storeEmail(storeEmail);
                if (shop == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "매장을 찾을 수 없습니다."));
                }
            }

            // 필드 업데이트
            switch (field) {
            case "shopInfo": 
        		shop.setShopInfo(value);
        		break;
            case "shopShortDesc": 
        		shop.setShopShortDesc(value);
        		break;
            case "shopLongDesc":
                shop.setShopLongDesc(value);
                break;
            case "shopAddress":
                shop.setShopAddress(value);
                break;
            case "shopPhone":
                shop.setShopPhone(value);
                break;
            case "shopWebsite":
                shop.setShopWebsite(value);
                break;
            case "shopFacility":
                shop.setShopFacilities(value);
                break;
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "잘못된 필드입니다."));
            }
        
        
            // 변경 사항 저장
            shopService.save(shop);

            return ResponseEntity.ok(Map.of("message", field + " 정보가 성공적으로 업데이트되었습니다."));

        } catch (Exception e) {
            logger.error("오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    
    @PostMapping("/update-hours")
    public ResponseEntity<?> updateShopHours(@RequestBody Map<String, String> request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String opentime = request.get("opentime");
            String closetime = request.get("closetime");

            if (opentime == null || closetime == null || opentime.isBlank() || closetime.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "영업 시간이 비어 있습니다."));
            }

            // 🔹 현재 로그인한 사업자의 이메일 가져오기
            String storeEmail = userDetails.getUsername();

            // 🔹 이메일을 기준으로 매장 조회
            Shop shop = shopService.findByStore_storeEmail(storeEmail);
            if (shop == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "매장을 찾을 수 없습니다."));
            }

            // 🔹 매장의 영업 시간 업데이트
            shop.setShopOpentime(opentime);
            shop.setShopClosetime(closetime);

            // 🔹 변경 사항 저장
            shopService.save(shop);

            return ResponseEntity.ok(Map.of("message", "영업 시간이 성공적으로 업데이트되었습니다."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "영업 시간 업데이트 중 오류 발생: " + e.getMessage()));
        }
    }

    
}