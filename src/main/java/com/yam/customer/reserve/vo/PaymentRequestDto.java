package com.yam.customer.reserve.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    private int paymentAmount;
    private String customerId;
    private Long shopId;
}