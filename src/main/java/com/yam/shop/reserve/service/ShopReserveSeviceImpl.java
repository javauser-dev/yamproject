package com.yam.shop.reserve.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.shop.reserve.domain.ShopReserve;
import com.yam.shop.reserve.repository.ShopReserveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopReserveSeviceImpl implements ShopReserveService {
	private final ShopReserveRepository shopReserveRepository;

	@Override
	public List<ShopReserve> shopReserveList(ShopReserve shopReserve) {
		List<ShopReserve> shopReserveList= shopReserveRepository.findAll();
		return shopReserveList;
	}

	@Override
	public void articleInsert(ShopReserve shopReserve) {
		shopReserveRepository.save(shopReserve);
		
	}

	@Override
	public ShopReserve articleDetail(ShopReserve shopReserve) {
		Optional<ShopReserve> articleOptional = shopReserveRepository.findById(shopReserve.getShopReserveNo());
		ShopReserve detail = articleOptional.get();
		return detail;
	}

	@Override
	public ShopReserve updateForm(ShopReserve shopReserve) {
		Optional<ShopReserve> articleOptional = shopReserveRepository.findById(shopReserve.getShopReserveNo());
		ShopReserve updateDate = articleOptional.get();
		
		return updateDate;
	}

	@Override
	public void articleUpdate(ShopReserve shopReserve) {
		Optional<ShopReserve> articleOptional = shopReserveRepository.findById(shopReserve.getShopReserveNo());
		ShopReserve updateArticle = articleOptional.get();
		updateArticle.setShopReserveRequest(shopReserve.getShopReserveRequest());
		updateArticle.setShopDate(shopReserve.getShopDate());
		shopReserveRepository.save(updateArticle);
	}

	@Override
	public void articleDelete(ShopReserve shopReserve) {
		shopReserveRepository.deleteById(shopReserve.getShopReserveNo());
		
	}

	@Override
	public PageResponseDTO<ShopReserve> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1페이지가 0이므로 주의
				pageRequestDTO.getSize(), Sort.by("shopReserveNo").descending());
		Page<ShopReserve> result = shopReserveRepository.findAll(pageable);
		List<ShopReserve> shopReserveList = result.getContent().stream().collect(Collectors.toList());
		long totalCount = result.getTotalElements();
		PageResponseDTO<ShopReserve> responseDTO = PageResponseDTO.<ShopReserve>withAll().dtoList(shopReserveList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

		return responseDTO;
	}
	/*
	 * @Override public PageResponseDTO<ShopReserve> list(PageRequestDTO
	 * pageRequestDTO) { int page = pageRequestDTO.getPage(); int size =
	 * pageRequestDTO.getSize();
	 * 
	 * // 페이지와 크기에 맞게 데이터 조회 List<ShopReserve> list =
	 * shopReserveMapper.getListWithPaging(pageRequestDTO); long totalCount =
	 * shopReserveMapper.getTotalCount();
	 * 
	 * return PageResponseDTO.<ShopReserve>withAll() .dtoList(list)
	 * .pageRequestDTO(pageRequestDTO) .totalCount(totalCount) .build(); }
	 */

}
