package com.yam.shop.reserve.service;

import java.util.List;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.shop.reserve.domain.ShopReserve;


public interface ShopReserveService {
	List<ShopReserve> shopReserveList(ShopReserve shopReserve);
	void articleInsert(ShopReserve shopReserve);
	ShopReserve articleDetail(ShopReserve shopReserve);
	ShopReserve updateForm(ShopReserve shopReserve);
	void articleUpdate(ShopReserve shopReserve);
	void articleDelete(ShopReserve shopReserve);
	PageResponseDTO<ShopReserve> list(PageRequestDTO pageRequestDTO);
	

}
