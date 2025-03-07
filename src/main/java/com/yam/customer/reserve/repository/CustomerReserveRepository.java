package com.yam.customer.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.reserve.domain.CustomerReserve;

public interface CustomerReserveRepository extends JpaRepository<CustomerReserve, Long> {

}
