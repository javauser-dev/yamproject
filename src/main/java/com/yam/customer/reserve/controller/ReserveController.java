package com.yam.customer.reserve.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yam.customer.reserve.repository.CustomerReserveRepository;
import com.yam.customer.reserve.service.PaymentService;
import com.yam.customer.reserve.service.ReserveService;
import com.yam.customer.reserve.vo.PaymentRequestDto;
import com.yam.customer.reserve.vo.ReserveRequestDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/reserve")
public class ReserveController {

    private final ReserveService reserveService;
    private final CustomerReserveRepository customerReserveRepository;
    private final PaymentService paymentService;  // PaymentService 주입
    //private final HttpSession httpSession; // 사용 안하면 제거해도 됨
    private static final Logger logger = LoggerFactory.getLogger(ReserveController.class);

    @GetMapping("/new")
    public String reserveForm(@RequestParam("shopId") Long shopId,
                              @RequestParam("reserveDate") String reserveDate,
                              @RequestParam("reserveTime") String reserveTime,
                              @RequestParam("guestCount") int guestCount,
                              @RequestParam("deposit") int deposit,
                              @RequestParam(value = "error", required = false) String error,
                              Model model, // Model 추가
                              HttpSession session) { // Authentication 제거

        // 세션에 예약 정보 저장 (reserveForm에서도 세션 사용)
        // session.setAttribute("customerId", customerId); // customerId는 reserveForm.js에서 처리
        session.setAttribute("shopId", shopId);
        session.setAttribute("reserveDate", reserveDate);
        session.setAttribute("reserveTime", reserveTime);
        session.setAttribute("guestCount", guestCount);
        session.setAttribute("deposit", deposit);


        // 모델에도 예약 정보 추가
        model.addAttribute("shopId", shopId);
        model.addAttribute("reserveDate", reserveDate);
        model.addAttribute("reserveTime", reserveTime);
        model.addAttribute("guestCount", guestCount);
        model.addAttribute("deposit", deposit);


        if (error != null) {
            if (error.equals("payment")) {
                model.addAttribute("errorMessage", "결제 처리 중 오류가 발생했습니다.");
            }
        }
        session.removeAttribute("paymentSuccess"); //혹시 남아있을 수 있는 paymentSuccess값은 지움.

        return "customer/reserve/reserveForm";
    }



     // AJAX 요청을 처리하여 세션에 데이터 저장 + DB 저장
    @PostMapping("/setPaymentInfo")
    @ResponseBody
    public String setPaymentInfo(
            @RequestParam("merchantPayKey") String merchantPayKey,
            @RequestParam("paymentAmount") int paymentAmount,
            @RequestParam("customerId") String customerId, // customerId 추가
            @RequestParam("shopId") Long shopId,       // shopId 추가
            HttpSession session) {

        // 1. 세션에 데이터 저장 (기존 로직)
        session.setAttribute("merchantPayKey", merchantPayKey);
        session.setAttribute("paymentAmount", paymentAmount);
        session.setAttribute("customerId", customerId); // customerId도 세션에 저장
        session.setAttribute("shopId", shopId);     // shopId도 세션에 저장.

        // 2. PaymentRequestDto 생성 및 값 설정
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentAmount(paymentAmount);
        paymentRequestDto.setCustomerId(customerId);
        paymentRequestDto.setShopId(shopId);


        // 3. PaymentService를 통해 결제 정보 DB에 저장
        try {
            paymentService.savePayment(paymentRequestDto);
        } catch (Exception e) {
            // DB 저장 중 오류 발생 시 처리 (로깅 등)
            System.err.println("결제 정보 저장 중 오류 발생: " + e.getMessage());
            return "error"; // 실패 응답
        }

        return "success"; // AJAX 요청 성공 응답
    }

    @GetMapping("/paymentSuccess")
    public String paymentSuccess(@RequestParam Map<String, String> allParams, HttpSession session, Model model) {
        // 네이버 페이에서 제공하는 정보
        String resultCode = allParams.get("resultCode");
        String paymentId = allParams.get("paymentId");
        String merchantPayKeyFromNaver = allParams.get("merchantPayKey"); // 사용
        String paymentAmountStrFromNaver = allParams.get("paymentAmount");

          int paymentAmountFromNaver = 0;
        if (paymentAmountStrFromNaver != null && !paymentAmountStrFromNaver.isEmpty()) {
          try{
            paymentAmountFromNaver = Integer.parseInt(paymentAmountStrFromNaver);
            } catch(NumberFormatException e) {
               System.err.println("paymentAmountFromNaver 변환 오류: " + paymentAmountStrFromNaver);
            }
          }


         // 세션에서 예약 정보 가져오기
        Long shopId = (Long) session.getAttribute("shopId");
        String reserveDate = (String) session.getAttribute("reserveDate");
        String reserveTime = (String) session.getAttribute("reserveTime");
        Integer guestCount = (Integer) session.getAttribute("guestCount");
        Integer deposit = (Integer) session.getAttribute("deposit");
        String customerId = (String) session.getAttribute("customerId");

        // ※ 세션에 merchantPayKey, paymentAmount 저장 (결제는 되었지만, paymentSuccess가 false로 남아있을 수 있는 경우 대비)
        //session.setAttribute("merchantPayKey", merchantPayKeyFromNaver); //주석 처리
        //session.setAttribute("paymentAmount", paymentAmountFromNaver); //주석 처리

        // 모델에 데이터 추가
        model.addAttribute("resultCode", resultCode);
        model.addAttribute("paymentId", paymentId);
        model.addAttribute("merchantPayKey", merchantPayKeyFromNaver);
        model.addAttribute("paymentAmount", paymentAmountFromNaver);


        // 세션에서 가져온 예약 정보 모델에 추가
        model.addAttribute("shopId", shopId);
        model.addAttribute("reserveDate", reserveDate);
        model.addAttribute("reserveTime", reserveTime);
        model.addAttribute("guestCount", guestCount);
        model.addAttribute("deposit", deposit);
        model.addAttribute("customerId", customerId);

        try{
            if("Success".equals(resultCode)) {

              //paymentService.savePayment(paymentAmountFromNaver, null, customerId, shopId); //주석 처리
              //session.setAttribute("paymentSuccess", true); //세션에 true로 저장, 주석 처리
              return "customer/reserve/paymentSuccess";

            } else {
               logger.error("결제 실패: resultCode={}, paymentId={}", resultCode, paymentId);
                //session.setAttribute("paymentSuccess", false); // 주석 처리
                model.addAttribute("resultCode", resultCode);  // 모델에 추가
                return  "customer/reserve/paymentError";  // 실패 페이지
            }
          }catch(Exception e) {
            logger.error("결제 정보 저장 실패: ", e);
            model.addAttribute("paymentSuccess", false);  // 모델에 추가
            return "redirect:/customer/reserve/paymentError";
          }
    }

    @PostMapping("/new")
     public ResponseEntity<?> createReserve(@RequestBody ReserveRequestDto requestDto, HttpSession session) {
        try {
            String customerId = (String) session.getAttribute("customerId"); // 세션에서 가져오기
            requestDto.setCustomerId(customerId);

            Long reserveId = reserveService.createReserve(requestDto, customerId);

            // 예약 성공 시, reserveDto를 세션에 저장.
            session.setAttribute("reserveDto", requestDto);

            return ResponseEntity.ok("success");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("예약 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("예약 중 오류 발생: " + e.getMessage());
        }
    }
    
    @GetMapping("/complete")
    public String reserveComplete(HttpSession httpSession, Model model) {
        String merchantPayKey = (String) httpSession.getAttribute("merchantPayKey");
        Integer paymentAmount = (Integer) httpSession.getAttribute("paymentAmount");
        ReserveRequestDto reserveDto = (ReserveRequestDto) httpSession.getAttribute("reserveDto");

        model.addAttribute("merchantPayKey", merchantPayKey);
        model.addAttribute("paymentAmount", paymentAmount);
        model.addAttribute("reserveDto", reserveDto);

        httpSession.removeAttribute("paymentSuccess");
        httpSession.removeAttribute("merchantPayKey");
        httpSession.removeAttribute("paymentAmount");
        //httpSession.removeAttribute("reserveDto"); // 주석 처리
        httpSession.removeAttribute("shopId");
          httpSession.removeAttribute("reserveDate");
        httpSession.removeAttribute("reserveTime");
        httpSession.removeAttribute("guestCount");
        httpSession.removeAttribute("deposit");
        httpSession.removeAttribute("customerId");  //customerId 삭제

        return "customer/reserve/reserveComplete";
    }

    @GetMapping("/paymentError")
    public String paymentError() {
        return "customer/reserve/paymentError";
    }

    // 세션에서 customerId 가져오는 엔드포인트 추가
    @GetMapping("/getCustomerId")
    @ResponseBody
    public String getCustomerId(HttpSession session) {
        String customerId = (String) session.getAttribute("customerId");

        //customerId가 세션에 없으면, Authentication에서 가져와 세션에 저장
        if(customerId == null || customerId.isEmpty()){
             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                 UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                 customerId = userDetails.getUsername();
                 session.setAttribute("customerId", customerId); //세션에도 저장
            }
        }
        return customerId != null ? customerId : ""; // customerId 반환 (없으면 빈 문자열)
    }
}