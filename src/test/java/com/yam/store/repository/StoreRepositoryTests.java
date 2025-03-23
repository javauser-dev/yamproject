package com.yam.store.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.yam.store.Store;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class StoreRepositoryTests {
	
	@Setter(onMethod_ = @Autowired)
	public StoreRepository storeRepository;
	
	@Test
    public void insertStoreData() {
        for (int i = 1; i <= 110; i++) {
            Store store = Store.builder()
                    .storeNickname("닉네임" + i)
                    .storeBusinessNumber("123-45-" + String.format("%05d", i)) // "123-45-00001" 형식
                    .storeName("가게이름" + i)
                    .storePassword("1234")
                    .storePhone("010-1234-" + String.format("%04d", i))  // "010-1234-0001" 형식
                    .storeEmail("test" + i + "@example.com")
                    .agree(true)
                    .enabled(true) // 또는 false, 필요에 따라
                    .emailVerified(true) // 또는 false, 필요에 따라
                    .businessVerified(true) //또는 false, 필요에 따라
                    .build();

            storeRepository.save(store);
        }
    }
}
