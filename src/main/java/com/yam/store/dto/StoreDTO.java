package com.yam.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class StoreDTO {
    private String storeName;
    private String storeNickname;
    private String storeEmail;
    private String storePassword;
    private String storePhone;
    private String storeBusinessNumber;
    private boolean agree;
}
