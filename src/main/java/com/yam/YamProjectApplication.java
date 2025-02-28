package com.yam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class YamProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(YamProjectApplication.class, args);
	}

}
