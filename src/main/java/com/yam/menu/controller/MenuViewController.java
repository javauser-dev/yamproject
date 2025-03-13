package com.yam.menu.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yam.menu.Menu;
import com.yam.menu.service.MenuService;

@Controller
@RequestMapping("/store/shop/menu")
public class MenuViewController {

    @Autowired
    private MenuService menuService;
    
    @Value("${file.upload.directory:upload}")
    private String uploadDirectory;
    
    @GetMapping("/menu")
    public String menuView(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        model.addAttribute("menus", menus);
        return "menu/menu";
    }
    
    @GetMapping("/edit/{id}")
    @ResponseBody
    public Menu getMenuForEdit(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }
    
    @PostMapping("/update/{id}")
    public String updateMenu(@PathVariable Long id,
                             @RequestParam("name") String name,
                             @RequestParam("simpleExp") String simpleExp,
                             @RequestParam("component") String component,
                             @RequestParam("price") String price,
                             @RequestParam("category") String category,
                             @RequestParam(value = "image", required = false) MultipartFile imageFile) throws IOException {
        
        Menu menu = menuService.getMenuById(id);
        if (menu == null) {
            // 에러 처리
            return "redirect:/store/shop/menu/menu";
        }
        
        menu.setName(name);
        menu.setSimpleExp(simpleExp);
        menu.setComponent(component);
        menu.setPrice(price);
        menu.setCategory(category);
        menu.setUpdatedAt(LocalDateTime.now());

        // 이미지 파일 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 파일 삭제
            if (menu.getFilename() != null && !menu.getFilename().isEmpty()) {
                File oldFile = new File(uploadDirectory + File.separator + menu.getFilename());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            
            // 새 파일 저장
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            imageFile.transferTo(destFile);

            menu.setFilename(newFilename);
        }

        menuService.saveMenu(menu);
        
        return "redirect:/store/shop/menu/menu";
    }
    
    @PostMapping("/menu/add")
    public String addMenu(@RequestParam String category, @RequestParam String name, @RequestParam String price) {
        System.out.println("카테고리: " + category);  // 카테고리 값 확인

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 값이 없습니다.");
        }

        Menu menu = new Menu();
        menu.setCategory(category);
        menu.setName(name);
        menu.setPrice(price);

        menuService.saveMenu(menu);

        return "redirect:/store/shop/menu/list";
    }
    
    @PostMapping("/save")
    public String saveMenu(@RequestParam("name") String name,
                           @RequestParam("simpleExp") String simpleExp,
                           @RequestParam("component") String component,
                           @RequestParam("price") String price,
                           @RequestParam("category") String category,
                           @RequestParam("image") MultipartFile imageFile) throws IOException {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setSimpleExp(simpleExp);
        menu.setComponent(component);
        menu.setPrice(price);
        menu.setCategory(category);

        // 이미지 파일 처리 로직...
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            imageFile.transferTo(destFile);

            menu.setFilename(newFilename);
        }

        LocalDateTime now = LocalDateTime.now();
        menu.setCreatedAt(now);
        menu.setUpdatedAt(now);

        menuService.saveMenu(menu);

        return "redirect:/store/shop/menu/menu";
    }


    
    @GetMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        
        // Delete image file if exists
        if (menu != null && menu.getFilename() != null && !menu.getFilename().isEmpty()) {
            File imageFile = new File(uploadDirectory + File.separator + menu.getFilename());
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
        
        // Delete from database
        menuService.deleteMenu(id);
        
        return "redirect:/store/shop/menu/menu";
    }
}