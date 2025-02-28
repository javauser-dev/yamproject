package com.yam.customer.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.reserve.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long>{

}
