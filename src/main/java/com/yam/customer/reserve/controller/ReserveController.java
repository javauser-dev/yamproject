package com.yam.customer.reserve.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ReserveController.class); // 로거 추가

    // 매장 페이지에서 예약 정보 받아서 예약 페이지로 이동 (GET)
    @GetMapping("/new")
    public String reserveForm(@RequestParam("shopId") Long shopId,
                              @RequestParam("reserveDate") String reserveDate,
                              @RequestParam("reserveTime") String reserveTime,
                              @RequestParam("guestCount") int guestCount,
                              @RequestParam("deposit") int deposit,
                              @RequestParam(value = "error", required = false) String error, // 에러 파라미터 추가
                              Model model,
                              Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String customerId = userDetails.getUsername();


        ReserveRequestDto reserveDto = new ReserveRequestDto();
        reserveDto.setShopId(shopId);
        reserveDto.setReserveDate(LocalDate.parse(reserveDate));
        reserveDto.setReserveTime(LocalTime.parse(reserveTime));
        reserveDto.setGuestCount(guestCount);
        reserveDto.setDeposit(deposit);

        model.addAttribute("reserveDto", reserveDto);
        model.addAttribute("customerId", customerId);

        // 에러 메시지가 있으면 모델에 추가
        if (error != null) {
            if (error.equals("payment")) {
                 model.addAttribute("errorMessage", "결제 처리 중 오류가 발생했습니다.");
            }
        }

        return "customer/reserve/reserveForm";
    }


    // 예약 처리 (POST) - createReserve 메서드
    @PostMapping("/new")
    public ResponseEntity<?> createReserve(@RequestBody ReserveRequestDto requestDto, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String customerId = userDetails.getUsername();

            requestDto.setCustomerId(customerId);
            Long reserveId = reserveService.createReserve(requestDto, customerId);

            // 성공 시 "success" 문자열 반환
            return ResponseEntity.ok("success"); // 또는 JSON 객체 등

        } catch (IllegalArgumentException e) {
            // 예약 실패 시 처리 (예: 에러 메시지 반환)
            return ResponseEntity.badRequest().body("예약 실패: " + e.getMessage());
        } catch (Exception e) {
            // 기타 오류 처리
            return ResponseEntity.internalServerError().body("예약 중 오류 발생: " + e.getMessage());
        }
    }


   // 네이버 페이 결제 성공 후 리다이렉트될 URL
   /*@GetMapping("/payment-success")
   public String paymentSuccess(@RequestParam("merchantPayKey") String merchantPayKey,
                                @RequestParam("paymentAmount") int paymentAmount,
                                Authentication authentication, HttpSession session) {

       UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       String customerId = userDetails.getUsername();

       Long shopId = (Long) session.getAttribute("shopId");
       String reserveDate = (String) session.getAttribute("reserveDate");
       String reserveTime = (String) session.getAttribute("reserveTime");
       Integer guestCount = (Integer) session.getAttribute("guestCount");
       Integer deposit = (Integer) session.getAttribute("deposit");


       try {
           // customerReserveId는 null로 전달 (예약 정보와 연결하지 않음)
           reserveService.savePayment(paymentAmount, null, customerId, shopId);

           // 세션에 결제 정보 저장 (선택 사항, 예약 완료 페이지에서 필요하면 사용)
           session.setAttribute("paymentSuccess", true);
           session.setAttribute("merchantPayKey", merchantPayKey);
           session.setAttribute("paymentAmount", paymentAmount);


           // 예약 폼으로 리다이렉트 (세션에 저장된 예약 정보를 쿼리 파라미터로 전달)
           return "redirect:/customer/reserve/new?shopId=" + shopId +
                  "&reserveDate=" + reserveDate +
                  "&reserveTime=" + reserveTime +
                  "&guestCount=" + guestCount +
                  "&deposit=" + deposit;


       } catch (Exception e) {
           // 결제 정보 저장 실패 시 처리
           logger.error("결제 정보 저장 실패: ", e); // 로그 기록 (logger는 이전 답변 참고)

           // 에러 메시지와 함께 예약 폼으로 리다이렉트 (선택 사항)
           return "redirect:/customer/reserve/new?error=payment&shopId=" + shopId +
                  "&reserveDate=" + reserveDate +
                  "&reserveTime=" + reserveTime +
                  "&guestCount=" + guestCount +
                   "&deposit=" + deposit;
       }
   }*/
    
    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam("merchantPayKey") String merchantPayKey,
                                 @RequestParam("paymentAmount") int paymentAmount,
                                 Authentication authentication, HttpSession session) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String customerId = userDetails.getUsername();

        Long shopId = (Long) session.getAttribute("shopId");
        String reserveDate = (String) session.getAttribute("reserveDate");
        String reserveTime = (String) session.getAttribute("reserveTime");
        Integer guestCount = (Integer) session.getAttribute("guestCount");
        Integer deposit = (Integer) session.getAttribute("deposit");


        try {
            // customerReserveId는 null로 전달 (예약 정보와 연결하지 않음)
            reserveService.savePayment(paymentAmount, null, customerId, shopId);

            // 세션에 결제 정보 저장 (선택 사항, 예약 완료 페이지에서 필요하면 사용)
            session.setAttribute("paymentSuccess", true);
            session.setAttribute("merchantPayKey", merchantPayKey);
            session.setAttribute("paymentAmount", paymentAmount);


            // 예약 폼으로 리다이렉트 (세션에 저장된 예약 정보를 쿼리 파라미터로 전달)
            return "redirect:/customer/reserve/new?shopId=" + shopId +
                   "&reserveDate=" + reserveDate +
                   "&reserveTime=" + reserveTime +
                   "&guestCount=" + guestCount +
                   "&deposit=" + deposit;


        } catch (Exception e) {
            // 결제 정보 저장 실패 시 처리
            logger.error("결제 정보 저장 실패: ", e); // 로그 기록 (logger는 이전 답변 참고)

            // 에러 메시지와 함께 예약 폼으로 리다이렉트 (선택 사항)
            return "redirect:/customer/reserve/new?error=payment&shopId=" + shopId +
                   "&reserveDate=" + reserveDate +
                   "&reserveTime=" + reserveTime +
                   "&guestCount=" + guestCount +
                    "&deposit=" + deposit;
        }
    }


   @GetMapping("/complete")
    public String reserveComplete(HttpSession httpSession, Model model) {
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