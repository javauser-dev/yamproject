package com.yam.shop.reserve.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.common.vo.PageRequestDTO;
import com.yam.shop.reserve.domain.ShopReserve;
import com.yam.shop.reserve.service.ShopReserveService;

@RequestMapping("/shopReserve/*")
@Controller
public class ShopReserveController { // 예약 목록 조회할거야 목록 html만들
	@Autowired
	private ShopReserveService shopReserveService;
	
	
	@GetMapping("/shopReserveList")
	public String shopReserveList(ShopReserve shopReserve, Model model) {
	    List<ShopReserve> shopReserveList = shopReserveService.shopReserveList(shopReserve);
	    if (shopReserveList == null) {
	        shopReserveList = new ArrayList<>(); // 리스트가 null일 경우 빈 리스트로 초기화
	    }
	    model.addAttribute("shopReserveList", shopReserveList); // 모델의 속성 이름 수정
	    return "client/shopReserve/shopReserveList";
	}
	
//	@GetMapping("/shopReserveList")
//	public String shopReserveList(PageRequestDTO pageRequestDTO, Model model) {
//	    try {
//	        PageResponseDTO<ShopReserve> result = shopReserveService.list(pageRequestDTO);
//	        model.addAttribute("shopReserveList", result);
//	        return "client/shopReserve/shopReserveList";
//	    } catch (Exception e) {
//	        e.printStackTrace(); // 로그 확인용
//	        model.addAttribute("errorMessage", "데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
//	        return "error/errorPage"; // 에러 페이지로 리다이렉트
//	    }
//	}
	
//	@GetMapping("/shopReserveList")
//	public String shopReserveList(PageRequestDTO pageRequestDTO, Model model) {
//	    PageResponseDTO<ShopReserve> responseDTO = shopReserveService.getList(pageRequestDTO);
//	    
//	    model.addAttribute("shopReserveList", responseDTO);
//	    
//	    return "client/shopReserve/shopReserveList";
//	}

	/* get이 값을 넣는거 */
	@GetMapping("/insertForm")
	public String insertForm(ShopReserve article) {
		return "client/shopReserve/insertForm";
	}
	
	/* post가 값을 보여주는거 */
	@PostMapping("/shopReserveInsert")
	public String articleInsert(ShopReserve article) {
		shopReserveService.articleInsert(article);
		return "redirect:/shopReserve/shopReserveList";
	}
	
//	/* 페이징 처리까지 된 애*/
//	@GetMapping("/shopReserveListPaged")  // 페이지네이션이 필요한 경우 다른 URL 사용
//	public String shopReserveListPaged(ShopReserve shopReserve, PageRequestDTO pageRequestDTO, Model model) {
//	    PageResponseDTO<ShopReserve> shopReserveListPaged = shopReserveService.list(pageRequestDTO);
//	    model.addAttribute("shopReserveListPaged", shopReserveListPaged);
//	    return "client/shopReserve/shopReserveList";
//	}
	
	// shopReserveListPaged 메서드는 제거하거나 리다이렉트로 변경
	@GetMapping("/shopReserveListPaged")
	public String shopReserveListPaged(PageRequestDTO pageRequestDTO) {
	    return "redirect:/shopReserve/shopReserveList?page=" + pageRequestDTO.getPage() + "&size=" + pageRequestDTO.getSize();
	}
	
//	@GetMapping("/{shopReserveNo}")
//	public String articleDetail(@PathVariable Long shopReserveNo, ShopReserve shopReserve, Model model) {
//		shopReserve.setShopReserveNo(shopReserveNo);
//		ShopReserve detail = shopReserveService.articleDetail(shopReserve);
//		model.addAttribute("detail", detail);
//	
//		String newLine = System.getProperty("line.separator").toString();
//		model.addAttribute("newLine", newLine);
//	
//		return "client/shopReserve/shopReserveDetail";
//	}
	
	@GetMapping("/{shopReserveNo}")
	public String articleDetail(@PathVariable(required = false) String shopReserveNo, ShopReserve shopReserve, Model model) {
	    try {
	        // shopReserveNo 값이 숫자가 아니라면 예외를 처리하도록 추가
	        Long reserveNo = Long.parseLong(shopReserveNo); // 예외 발생 가능성 있음
	        shopReserve.setShopReserveNo(reserveNo);
	        
	        ShopReserve detail = shopReserveService.articleDetail(shopReserve);
	        model.addAttribute("detail", detail);
	        
	        String newLine = System.getProperty("line.separator").toString();
	        model.addAttribute("newLine", newLine);

	        return "client/shopReserve/shopReserveDetail";
	    } catch (NumberFormatException e) {
	        // 숫자가 아닌 값이 전달된 경우 처리
	        return "error"; // 예외 발생 시 error 페이지로 이동
	    }
	}
	
	@PostMapping("/updateForm")
	public String updateForm(ShopReserve shopReserve) {
		shopReserveService.articleUpdate(shopReserve);
		return "redirect:/shopReserve/"+shopReserve.getShopReserveNo();
		
	}
	
	@PostMapping("/articleUpdate")
	public String articleUpdate(ShopReserve shopReserve) {
		shopReserveService.articleUpdate(shopReserve);
		return "redirect:/shopReserve/"+shopReserve.getShopReserveNo();
		
	}
	
	@PostMapping("/articleDelete")
	public String articleDelete(ShopReserve article) {
		shopReserveService.articleDelete(article);
		return "redirect:/shopReserve/shopReserveList";
	}

}
