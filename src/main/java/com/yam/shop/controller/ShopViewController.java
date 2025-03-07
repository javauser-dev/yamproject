package com.yam.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yam.shop.Shop;
import com.yam.shop.repository.ShopRepository;
import com.yam.shop.service.ShopService;

@Controller
@RequestMapping("/shop")
public class ShopViewController {
	@Autowired
    private ShopRepository shopRepository;
	private ShopService shopService;

    @GetMapping("/new")
    public String newShop(Model model) {
        model.addAttribute("shop", new Shop());
        return "shop/newShop";
    }
    
    @GetMapping("/shopList")
    public String shopList(Model model) {
        model.addAttribute("shop", new Shop());
        return "shop/shopList";
    }

    @PostMapping("/save")
    public String saveShop(Shop shop) {
        shopRepository.save(shop);
        return "redirect:/shop/list";
    }
    
    @GetMapping("/myShop")
    public String viewShop(@RequestParam Long shopNo, Model model) {
        Shop shop = shopService.getShop(shopNo);
        model.addAttribute("shop", shop); // shop 객체를 뷰로 전달
        return "shop/myShop"; // 템플릿 이름 반환
    }
}
