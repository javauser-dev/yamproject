package com.yam.admin.info.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.service.MemberService;
import com.yam.store.Store;
import com.yam.store.service.StoreService;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/info") // URL 매핑
public class AdminInfoController {
 
    private final MemberService memberService;
    private final StoreService storeService;
   
    @Autowired // 또는 private final ServletContext servletContext; 와 @RequiredArgsConstructor
    private ServletContext servletContext;

    // 정적 리소스 경로.  src/main/resources/static/upload
    private final String UPLOAD_DIR = "static/upload/";
    
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/main")
    public String mainPage() {
        return "admin/info/main";
    }

    @GetMapping("/members")
    public String memberList(Model model,
                             @PageableDefault(size = 10, sort = "customerId", direction = Sort.Direction.ASC) Pageable pageable,
                             @RequestParam(name = "searchType", required = false, defaultValue = "id") String searchType, // 검색 유형
                             @RequestParam(name = "searchKeyword", required = false) String searchKeyword) { // 검색어

        Page<Member> members;

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            // 검색어가 있으면 검색
            members = memberService.searchMembers(searchType, searchKeyword, pageable);
        } else {
            // 검색어가 없으면 전체 목록
            members = memberService.findAllMembersSortById(pageable);
        }

        model.addAttribute("members", members);
        model.addAttribute("searchType", searchType);       // 검색 유형
        model.addAttribute("searchKeyword", searchKeyword);  // 검색어 유지

        return "admin/info/memberList";
    }
    
    @GetMapping("/memberDetail")
    public String memberDetail(@RequestParam("customerId") String customerId, Model model) {
        Member member = memberService.getMemberById(customerId);
        if (member == null) {
            // ID에 해당하는 회원이 없으면, 에러 페이지 또는 목록으로 리다이렉트
            return "redirect:/admin/info/members"; // 예: 목록으로 리다이렉트
        }
        model.addAttribute("member", member);
        return "admin/info/memberDetail"; // memberDetail.html 뷰를 반환
    }
    
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("customerId") String customerId,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {

        memberService.adminUpdatePassword(customerId, passwordEncoder.encode(newPassword)); // 암호화
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/admin/info/memberDetail?customerId=" + customerId;
    }
    
    // 닉네임 중복 검사 (Ajax)
    @GetMapping("/checkNickname")
    @ResponseBody  // JSON 응답을 위해 추가
    public ResponseEntity<String> checkNickname(@RequestParam("nickname") String nickname,
                                                @RequestParam("customerId") String customerId) { // 현재 사용자의 ID도 받음
        
        // 현재 사용자의 닉네임과 같다면 중복이 아님.
        Member currentMember = memberService.getMemberById(customerId);
        if (currentMember != null && currentMember.getCustomerNickname().equals(nickname)) {
            return ResponseEntity.ok("valid"); // 유효한 닉네임
        }
        
        // 다른 사용자가 이미 사용하고 있는 닉네임인지 확인
        boolean isDuplicate = memberService.adminIsNicknameDuplicated(nickname);
        if (isDuplicate) {
            return ResponseEntity.ok("duplicate"); // 중복 닉네임
        } else {
            return ResponseEntity.ok("valid"); // 사용 가능한 닉네임
        }
    }
    
    // 닉네임 수정
    @PostMapping("/updateNickname")
    public String updateNickname(@RequestParam("customerId") String customerId,
                                 @RequestParam("newNickname") String newNickname,
                                 RedirectAttributes redirectAttributes) { // RedirectAttributes 추가
        memberService.adminUpdateNickname(customerId, newNickname);
        redirectAttributes.addFlashAttribute("message", "닉네임이 변경되었습니다."); // Flash Attribute 추가
        return "redirect:/admin/info/memberDetail?customerId=" + customerId;
    }

    // 이메일 수정
    @PostMapping("/updateEmail")
    public String updateEmail(@RequestParam("customerId") String customerId,
                              @RequestParam("newEmail") String newEmail,
                              RedirectAttributes redirectAttributes) { // RedirectAttributes 추가
        memberService.adminUpdateEmail(customerId, newEmail);
        redirectAttributes.addFlashAttribute("message", "이메일이 변경되었습니다."); // Flash Attribute 추가
        return "redirect:/admin/info/memberDetail?customerId=" + customerId;
    }

    // 프로필 사진 수정
    @PostMapping("/updateProfileImage")
    public String updateProfileImage(@RequestParam("customerId") String customerId,
                                     @RequestParam("newProfileImage") MultipartFile file,
                                     RedirectAttributes redirectAttributes) {

        if (!file.isEmpty()) {
            try {
                // 1. 파일 이름 생성 (고유화, 확장자 검사)
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                String fileExtension = "";
                if (originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    if (!fileExtension.toLowerCase().matches("\\.(jpg|jpeg|png|gif)$")) {
                        redirectAttributes.addFlashAttribute("errorMessage", "이미지 파일 (jpg, jpeg, png, gif)만 업로드 가능합니다.");
                        return "redirect:/admin/info/memberDetail?customerId=" + customerId;
                    }
                }
                String saveFileName = UUID.randomUUID().toString() + fileExtension;

                // 2. 저장할 경로 생성 (ClassPathResource 사용)
                Resource resource = new ClassPathResource(UPLOAD_DIR);
                String uploadPath = resource.getFile().getAbsolutePath();

                if (!new File(uploadPath).exists()) {
                    new File(uploadPath).mkdirs();
                }
                Path filePath = Paths.get(uploadPath, saveFileName);
                System.out.println("저장 경로: " + filePath); // 저장 경로 확인 (디버깅)

                // 3. 파일 저장
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


                // 4. DB에 저장할 URL
                String imageUrl = "/upload/" + saveFileName;  // URL 경로.  /upload/로 시작해야함!
                memberService.updateProfileImage(customerId, imageUrl);

                redirectAttributes.addFlashAttribute("message", "프로필 사진이 변경되었습니다.");

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/admin/info/memberDetail?customerId=" + customerId;
            }
        }

        return "redirect:/admin/info/memberDetail?customerId=" + customerId;
    }
    
    @GetMapping("/stores")  // main.html에서 연결될 URL
    public String storeList(Model model,
                            @PageableDefault(size = 10, sort = "storeNo", direction = Sort.Direction.ASC) Pageable pageable,
                            @RequestParam(name = "searchType", required = false, defaultValue = "storeName") String searchType,
                            @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {

        Page<Store> stores;

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            stores = storeService.adminSearchStores(searchType, searchKeyword, pageable);
        } else {
            stores = storeService.adminFindAllStores(pageable);
        }

        model.addAttribute("stores", stores);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        return "admin/info/storeList"; // 뷰 이름 (아래에서 생성)
    }
    
    @GetMapping("/storeDetail")
    public String storeDetail(@RequestParam("storeNo") Long storeNo, Model model) {
        Store store = storeService.adminGetStoreByNo(storeNo); // storeNo로 가게 정보 가져오기
        if (store == null) {
            // 가게 정보가 없으면 목록으로 리다이렉트 (또는 에러 페이지)
            return "redirect:/admin/info/stores";
        }
        model.addAttribute("store", store);
        return "admin/info/storeDetail"; // storeDetail.html 뷰 반환
    }
    
    @GetMapping("/checkStoreNickname")
    @ResponseBody // JSON 응답
    public ResponseEntity<String> checkStoreNickname(@RequestParam("nickname") String nickname,
                                                   @RequestParam("storeNo") Long storeNo) {

        // 현재 가게 닉네임과 같다면 중복 아님.
        Store currentStore = storeService.adminGetStoreByNo(storeNo);
        if(currentStore != null && currentStore.getStoreNickname().equals(nickname)){
            return ResponseEntity.ok("valid");
        }

        boolean isDuplicate = storeService.adminIsStoreNicknameDuplicated(nickname);
        if (isDuplicate) {
            return ResponseEntity.ok("duplicate"); // 중복
        } else {
            return ResponseEntity.ok("valid"); // 사용 가능
        }
    }
    
    @PostMapping("/updateStoreNickname")
    public String updateStoreNickname(@RequestParam("storeNo") Long storeNo,
                                      @RequestParam("newNickname") String newNickname,
                                      RedirectAttributes redirectAttributes) {

        storeService.adminUpdateStoreNickname(storeNo, newNickname);
        redirectAttributes.addFlashAttribute("message", "닉네임이 변경되었습니다.");
        return "redirect:/admin/info/storeDetail?storeNo=" + storeNo;
    }
    
    @PostMapping("/updateStorePassword")
    public String updateStorePassword(@RequestParam("storeNo") Long storeNo,
                                        @RequestParam("newPassword") String newPassword,
                                        RedirectAttributes redirectAttributes){

        storeService.adminUpdateStorePassword(storeNo, passwordEncoder.encode(newPassword)); // 암호화
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/admin/info/storeDetail?storeNo=" + storeNo;
    }
    
    @PostMapping("/updateStoreName")
    public String updateStoreName(@RequestParam("storeNo") Long storeNo,
                                  @RequestParam("newStoreName") String newStoreName,
                                  RedirectAttributes redirectAttributes) {

        storeService.adminUpdateStoreName(storeNo, newStoreName);
        redirectAttributes.addFlashAttribute("message", "가게 이름이 변경되었습니다.");
        return "redirect:/admin/info/storeDetail?storeNo=" + storeNo;
    }
    
    @PostMapping("/updateStoreEmail")
    public String updateStoreEmail(@RequestParam("storeNo") Long storeNo,
                                   @RequestParam("newEmail") String newEmail,
                                   RedirectAttributes redirectAttributes) {

        storeService.adminUpdateStoreEmail(storeNo, newEmail);
        redirectAttributes.addFlashAttribute("message", "이메일이 변경되었습니다.");
        return "redirect:/admin/info/storeDetail?storeNo=" + storeNo;
    }
}