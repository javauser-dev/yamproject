package com.yam.store.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.store.WithdrawnStore;

public interface WithdrawnStoreRepository extends JpaRepository<WithdrawnStore, Long>{
	void deleteByWithdrawalCompletedAtBefore(LocalDateTime dateTime);
	void deleteByWithdrawalCompletedAtBetween(LocalDateTime start, LocalDateTime end); // 추
	
		
}
