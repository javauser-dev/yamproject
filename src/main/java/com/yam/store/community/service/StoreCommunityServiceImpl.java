package com.yam.store.community.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.store.community.domain.StoreCommunity;
import com.yam.store.community.repository.StoreCommunityRepository;

import lombok.Setter;

@Service
public class StoreCommunityServiceImpl implements StoreCommunityService {
	
	@Setter(onMethod_=@Autowired)
	private StoreCommunityRepository storeCommunityRepository;

	@Override
	public List<StoreCommunity> storeCommunityList(StoreCommunity storeCommunity) {
		List<StoreCommunity> storeCommunityList = null;
//		storeCommunityList = (List<StoreCommunity>) boardRepository.findAll();
		storeCommunityList = storeCommunityRepository.findByOrderByStoreListNoDesc();
		return storeCommunityList;
	}

	@Override
	public void storeCommunityInsert(StoreCommunity storeCommunity) {
		storeCommunityRepository.save(storeCommunity);
		
	}

	@Override
	public void storeCommunityHitUpdate(StoreCommunity storeCommunity) {
		StoreCommunity dataBoard = getStoreCommunity(storeCommunity.getStoreListNo());
		dataBoard.setStoreHit(dataBoard.getStoreHit() + 1);
		storeCommunityRepository.save(dataBoard);
		
	}

	@Override
	public StoreCommunity storeCommunityDetail(StoreCommunity storeCommunity) {
		storeCommunityHitUpdate(storeCommunity);
		Optional<StoreCommunity> boardOptional = storeCommunityRepository.findById(storeCommunity.getStoreListNo());
		StoreCommunity detail = boardOptional.orElseThrow();
		return detail;
	}

	@Override
	public StoreCommunity getStoreCommunity(Long no) {
		Optional<StoreCommunity> boardOptional = storeCommunityRepository.findById(no);
		StoreCommunity updateData = boardOptional.orElseThrow();
		return updateData;
	}

	@Override
	public void storeCommunityUpdate(StoreCommunity storeCommunity) {
		Optional<StoreCommunity> boardOptional = storeCommunityRepository.findById(storeCommunity.getStoreListNo());
		StoreCommunity updateBoard = boardOptional.orElseThrow();

		updateBoard.setStoreTitle(storeCommunity.getStoreTitle());
		updateBoard.setStoreContent(storeCommunity.getStoreContent());
		if (!storeCommunity.getFilename().isEmpty()) {
			updateBoard.setFilename(storeCommunity.getFilename());
		}
		if(!storeCommunity.getPasswd().isEmpty()) {
			updateBoard.setPasswd(storeCommunity.getPasswd());
		}

		storeCommunityRepository.save(updateBoard);
		
	}

	@Override
	public void storeCommunityDelete(StoreCommunity storeCommunity) {
		storeCommunityRepository.deleteById(storeCommunity.getStoreListNo());
		
	}

	@Override
	public PageResponseDTO<StoreCommunity> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1페이지가 0이므로 주의
				pageRequestDTO.getSize(), Sort.by("storeListNo").descending());
		Page<StoreCommunity> result = storeCommunityRepository.findAll(pageable);
		List<StoreCommunity> storeCommunityList = result.getContent().stream().collect(Collectors.toList());
		long totalCount = result.getTotalElements();
		PageResponseDTO<StoreCommunity> responseDTO = PageResponseDTO.<StoreCommunity>withAll().dtoList(storeCommunityList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

		return responseDTO;
	}

	

}
