package com.yam.customer.reserve.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.reserve.domain.CustomerReserve;
import com.yam.shop.Shop;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class CustomerRepositoryTests {
	
	@Setter(onMethod_ = @Autowired)
	public CustomerReserveRepository customerReserveRepository;
	
	@Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Shop testShop;
    private Member testMember;

    @BeforeEach
    public void setUp() {
        // 테스트용 Shop 조회 (또는 생성)
        //  - 이미 존재하는 shopNo가 "1"인 Shop을 사용.
        //  - 만약 "1"번 Shop이 없으면 예외 발생 또는 새로운 Shop 생성.
        testShop = shopRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Shop with id 1 not found"));

        // 테스트용 Member 조회 (또는 생성)
        //  - 이미 존재하는 id가 "id3"인 Member를 사용.
        // - 만약 "id3" Member가 없다면, 예외 발생 또는 새로운 Member 생성
        testMember = memberRepository.findById("id3")
                .orElseThrow(() -> new IllegalArgumentException("Member with id id3 not found"));
    }
	
	@Test
	public void reserveAllInsertTest() {
		Random random = new Random();
		
		for(int i = 1; i < 110; i++) {
			//날짜, 시간 랜덤 생성
            LocalDate randomDate = LocalDate.now().plusDays(random.nextInt(30)); // 오늘부터 30일 이내
            LocalTime randomTime = LocalTime.of(random.nextInt(12) + 10, 0); // 10:00 ~ 21:00
            
			CustomerReserve reserve = new CustomerReserve();
			reserve.setDeposit(50000);
			reserve.setGuestCount(4);
			reserve.setReserveDate(LocalDate.of(2025, 1, 1).plusDays(i));
            reserve.setReserveTime(randomTime);      //랜덤 시간
            reserve.setReserveCheck(0);
            reserve.setReserveCancel(0);
            reserve.setShop(testShop);
            reserve.setMember(testMember);
			
            customerReserveRepository.save(reserve);
		}
	}
}
