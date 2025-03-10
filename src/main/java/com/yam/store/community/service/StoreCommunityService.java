package com.yam.store.community.service;

import java.util.List;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.store.community.domain.StoreCommunity;


public interface StoreCommunityService {
	public List<StoreCommunity> storeCommunityList(StoreCommunity storeCommunity);
	public void storeCommunityInsert(StoreCommunity storeCommunity);
	public void storeCommunityHitUpdate(StoreCommunity storeCommunity);
	public StoreCommunity storeCommunityDetail(StoreCommunity storeCommunity);
	public StoreCommunity getStoreCommunity(Long storeListNo);
	public void storeCommunityUpdate(StoreCommunity storeCommunity);
	public void storeCommunityDelete(StoreCommunity storeCommunity);
	public PageResponseDTO<StoreCommunity> list(PageRequestDTO pageRequestDTO);

}
