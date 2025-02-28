package com.yam.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yam.login.CustomUserDetails;
import com.yam.store.Store;
import com.yam.store.email.controller.StoreRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	 private final StoreRepository storeRepository;

	    public CustomUserDetailsService(StoreRepository storeRepository) {
	        this.storeRepository = storeRepository;
	    }

	    @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	        Store store = storeRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다: " + email));

	        return new CustomUserDetails(store); // User 대신 CustomUserDetails 반환
	    }
}
