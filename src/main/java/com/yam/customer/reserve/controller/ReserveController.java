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

import com.yam.customer.reserve.domain.ReservationPayment;
import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.service.PaymentService;
import com.yam.customer.reserve.service.ReserveService;
import com.yam.customer.reserve.vo.ReserveRequestDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/reserve")
public class ReserveController {

    private final ReserveService reserveService;
    private final CustomerReserveRepository customerReserveRepository; // 필드 추가
    private final PaymentService paymentService;
    private final HttpSession httpSession;

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
    
    @GetMapping("/payment-success")  // GetMapping으로 변경
    public String paymentSuccess(@RequestParam("merchantPayKey") String merchantPayKey,
                                 @RequestParam("paymentAmount") int paymentAmount,
                                 Authentication authentication) {

        // Authentication 객체에서 UserDetails 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // UserDetails에서 username (여기서는 회원 ID로 사용) 가져오기
        String customerId = userDetails.getUsername();

        // TODO: merchantPayKey를 사용하여 추가적인 검증 로직을 수행할 수 있습니다. (선택 사항)

        // 결제 정보 저장 로직 호출 (PaymentController를 사용하지 않고 직접 처리)
        try {
               // customerReserveId를 가져오는 로직 (가장 최근 예약 ID + 1)
                Long nextReserveId = customerReserveRepository.findMaxReserveId()
                        .map(id -> id + 1)
                        .orElse(1L); // 예약 기록이 없으면 1부터 시작
                // DTO에서 필요한 정보 추출, ReservationPayment 객체 생성

              ReservationPayment payment = new ReservationPayment(
                    paymentAmount,
                    nextReserveId, // 다음 예약 ID
                    customerId,
                    Long.parseLong((String) httpSession.getAttribute("shopId")) //세션에서 shopId 가져옴
              );

            paymentService.savePayment(payment); //결제 정보 저장


            // 세션에 예약 정보 임시 저장 (선택 사항)
            // - 예약 완료 페이지에서 이 정보를 사용할 수 있습니다.
            httpSession.setAttribute("paymentSuccess", true);
            httpSession.setAttribute("merchantPayKey", merchantPayKey);
            httpSession.setAttribute("paymentAmount", paymentAmount);



            return "redirect:/customer/reserve/complete";  // 예약 완료 페이지로 리다이렉션

        } catch (Exception e) {
            // 결제 정보 저장 실패 시 처리 (예: 로깅, 에러 페이지 리다이렉션)
            return "redirect:/customer/reserve/payment-error"; // 에러페이지
        }
    }


       @GetMapping("/complete")
        public String reserveComplete(HttpSession httpSession, Model model) {
            // 세션에서 결제 정보 확인
            Boolean paymentSuccess = (Boolean) httpSession.getAttribute("paymentSuccess");
            if (paymentSuccess == null || !paymentSuccess) {
                // 결제 정보가 없거나 실패한 경우 처리 (예: 에러 페이지 리다이렉션)
                return "redirect:/"; // 또는 다른 적절한 처리
            }


            // 세션에서 merchantPayKey와 paymentAmount 가져오기 (필요한 경우)
            String merchantPayKey = (String) httpSession.getAttribute("merchantPayKey");
            Integer paymentAmount = (Integer) httpSession.getAttribute("paymentAmount");

            // 모델에 필요한 정보 추가 (예: 결제 정보, 예약 정보 등)
            model.addAttribute("merchantPayKey", merchantPayKey);
            model.addAttribute("paymentAmount", paymentAmount);

            // 세션 정보 삭제 (더 이상 필요하지 않은 경우)
            httpSession.removeAttribute("paymentSuccess");
            httpSession.removeAttribute("merchantPayKey");
            httpSession.removeAttribute("paymentAmount");
            httpSession.removeAttribute("shopId");

            // 예약 완료 페이지 (reserveComplete.html)로 이동
            return "customer/reserve/reserveComplete"; // 뷰 이름
        }
}