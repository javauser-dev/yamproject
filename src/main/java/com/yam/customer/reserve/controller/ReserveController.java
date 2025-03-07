package com.yam.customer.reserve.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yam.customer.reserve.service.ReserveService;
import com.yam.customer.reserve.vo.ReserveRequestDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/reserve")
public class ReserveController {

    private final ReserveService reserveService;

    // 매장 페이지에서 예약 정보 받아서 예약 페이지로 이동 (GET)
    @GetMapping("/new")
    public String reserveForm(@RequestParam("shopId") Long shopId,
                              @RequestParam("reserveDate") String reserveDate,
                              @RequestParam("reserveTime") String reserveTime,
                              @RequestParam("guestCount") int guestCount,
                              @RequestParam("deposit") int deposit, // deposit 파라미터 추가
                              Model model,
                              Authentication authentication) { // Authentication 추가

        // Authentication 객체에서 UserDetails 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // UserDetails에서 username (여기서는 회원 ID로 사용) 가져오기
        String customerId = userDetails.getUsername();

        // ReserveRequestDto를 만들어서 모델에 추가
        ReserveRequestDto reserveDto = new ReserveRequestDto();
        reserveDto.setShopId(shopId);
        reserveDto.setReserveDate(LocalDate.parse(reserveDate)); // String -> LocalDate
        reserveDto.setReserveTime(LocalTime.parse(reserveTime)); // String -> LocalTime
        reserveDto.setGuestCount(guestCount);
        reserveDto.setDeposit(deposit); // deposit 설정

        model.addAttribute("reserveDto", reserveDto);
        model.addAttribute("customerId", customerId); // 회원 ID를 모델에 추가

        return "customer/reserve/reserveForm"; // 예약 폼 페이지 (Thymeleaf 템플릿)
    }

    // 예약 처리 (POST)
    @PostMapping("/new")
    public ResponseEntity<?> createReserve(@RequestBody ReserveRequestDto requestDto, Authentication authentication) { // Authentication 추가
        try {
            // Authentication 객체에서 UserDetails 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // UserDetails에서 username (여기서는 회원 ID로 사용) 가져오기
            String customerId = userDetails.getUsername();

            // 회원 ID를 requestDto에 설정
            requestDto.setCustomerId(customerId);
            
            Long reserveId = reserveService.createReserve(requestDto);
            return new ResponseEntity<>("예약이 완료되었습니다. 예약 번호: " + reserveId, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
             return new ResponseEntity<>("예약 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 기타 필요한 메소드 (예약 확인, 수정, 삭제 등)
}