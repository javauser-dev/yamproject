package com.yam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 활성화
public class YamProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(YamProjectApplication.class, args);
	}

}
