package com.yam.shop.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.shop.reserve.domain.ShopReserve;

public interface ShopReserveRepository extends JpaRepository<ShopReserve, Long> {
//	@Modifying
//	@Query("DELETE FROM Comment c WHERE c.id = ?1")
//	public void deleteComment(Long no);
//	
//	@Modifying
//	@Query("UPDATE Comment c SET c.body = ?2 WHERE c.id = ?1")
//	public void updateComment(Long no, String body);

}
