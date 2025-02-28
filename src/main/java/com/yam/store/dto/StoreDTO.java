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
    private String name;
    private String email;
    private String password;
    private String phone;
    private String businessNumber;
    private boolean agree;
}
