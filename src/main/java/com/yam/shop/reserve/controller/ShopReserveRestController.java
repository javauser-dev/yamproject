package com.yam.shop.reserve.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.shop.reserve.domain.ShopReserve;
import com.yam.shop.reserve.service.ShopReserveService;

import lombok.Setter;

@RestController
@RequestMapping("/api/shopReserve/*")
@CrossOrigin
public class ShopReserveRestController {
	
	@Setter(onMethod_ = @Autowired)
	private ShopReserveService shopReserveService;
	
	@GetMapping("/list")
	public PageResponseDTO<ShopReserve> list(PageRequestDTO pageRequestDTO){
		return shopReserveService.list(pageRequestDTO);
	}

}
