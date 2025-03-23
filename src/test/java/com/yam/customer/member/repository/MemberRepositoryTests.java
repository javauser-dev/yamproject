package com.yam.customer.member.repository;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.yam.customer.member.domain.Member;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class MemberRepositoryTests {

	@Setter(onMethod_ = @Autowired)
	public MemberRepository memberRepository;
		
	//@Test
	public void memeberAllInsertTest() {
		for(int i = 4; i < 104; i++) {
			Member member = new Member();
			member.setCustomerId("user" + i);
			member.setCustomerPassword("1234");
			member.setCustomerNickname("닉네임" + i);
			member.setCustomerName("홍길동" + i);
			member.setCustomerEmail("javauser@gmail.com");
			member.setCustomerBirthDate(LocalDate.of(2000, 1, 1).plusDays(i));
			member.setCustomerGender(i % 2 == 0 ? "F" : "M");
			member.setCustomerApproval("Y");
			
			memberRepository.save(member);
			
			
		}
	}
}
