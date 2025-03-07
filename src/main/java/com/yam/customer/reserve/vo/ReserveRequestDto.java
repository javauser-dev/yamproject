package com.yam.customer.reserve.vo;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자
@Builder
public class ReserveRequestDto {
    private Long shopId;
    private LocalDate reserveDate;
    private LocalTime reserveTime;
    private int guestCount;
    private int deposit;
    private String request;
    private String customerId; // 회원 ID
}