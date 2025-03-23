package com.yam.store.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.store.Store;
import com.yam.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

	private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public Page<Store> adminFindAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }


    @Transactional(readOnly = true)
    public Page<Store> adminSearchStores(String searchType, String searchKeyword, Pageable pageable) {
        // 검색 로직 (MemberService의 searchMembers 참고하여 구현)
        if ("storeName".equals(searchType)) {
            //return storeRepository.findByStoreNameContaining(searchKeyword, pageable); // storeName으로 검색한다면 이렇게.
           // Containing 사용시 대소문자 구별없이 검색하려면
            List<Store> stores = storeRepository.findAll(); // 임시로 전체 불러옴
            stores = stores.stream().filter(s -> s.getStoreName().toLowerCase().contains(searchKeyword.toLowerCase())).toList();
            return new PageImpl<>(stores, pageable, stores.size());


        } else if ("storeBusinessNumber".equals(searchType)) {
            //return storeRepository.findByStoreBusinessNumberContaining(searchKeyword, pageable); // 사업자 번호로 검색
            List<Store> stores = storeRepository.findAll(); // 임시로 전체 불러옴
            stores = stores.stream().filter(s -> s.getStoreBusinessNumber().contains(searchKeyword)).toList();
            return new PageImpl<>(stores, pageable, stores.size());

        }  else if ("storeNickname".equals(searchType)) {
            List<Store> stores = storeRepository.findAll(); // 임시로 전체 불러옴
            stores = stores.stream().filter(s -> s.getStoreNickname().toLowerCase().contains(searchKeyword.toLowerCase())).toList();
            return new PageImpl<>(stores, pageable, stores.size());

          }else {
            // 다른 검색 유형 추가
            return Page.empty(pageable); // 빈 페이지 반환
        }
    }
    
    @Transactional(readOnly = true)
    public Store adminGetStoreByNo(Long storeNo) {
        return storeRepository.findById(storeNo).orElse(null); // Optional 처리
    }
    
    // 닉네임 중복 확인
    public boolean adminIsStoreNicknameDuplicated(String nickname) {
        return storeRepository.existsByStoreNickname(nickname);
    }
    
    public void adminUpdateStoreNickname(Long storeNo, String newNickname) {
        Store store = storeRepository.findById(storeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeNo)); // 예외 처리

        store.setStoreNickname(newNickname);
        // storeRepository.save(store); // @Transactional에 의해 자동 저장
    }
    
    public void adminUpdateStorePassword(Long storeNo, String newPassword){
        Store store = storeRepository.findById(storeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다 id=" + storeNo));

        store.setStorePassword(newPassword);
        // storeRepository.save(store); // @Transactional에 의해 자동 저장
    }
    
    public void adminUpdateStoreName(Long storeNo, String newStoreName) {
        Store store = storeRepository.findById(storeNo)
            .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeNo));

        store.setStoreName(newStoreName);
        // storeRepository.save(store); // @Transactional에 의해 자동 저장
    }
    
    public void adminUpdateStoreEmail(Long storeNo, String newEmail) {
        Store store = storeRepository.findById(storeNo)
            .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다. id=" + storeNo));

        store.setStoreEmail(newEmail);
        // storeRepository.save(store); // @Transactional에 의해 자동 저장
    }
}
