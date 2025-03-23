package com.yam.customer.reserve.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.yam.shop.Shop;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class ShopRepositoryTest {
	
	@Autowired
    private ShopRepository shopRepository;

    @Test
    void testFindById() {
        // shopNo가 1인 데이터가 DB에 있다고 가정
        Shop shop = shopRepository.findById(1L).orElse(null);
        log.info(shop.toString());
        
    }

}
