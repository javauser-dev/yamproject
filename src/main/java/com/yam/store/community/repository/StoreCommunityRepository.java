package com.yam.store.community.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.store.community.domain.StoreCommunity;

public interface StoreCommunityRepository extends JpaRepository<StoreCommunity, Long> {

	/* Spring Data JPA 는 메서드의 이름만으로 원하는 질의(QUERY)를 실행할 수 있는 방법을 제공한다. findBy */

	// 번호랑 비번
	StoreCommunity findByStoreListNoAndPasswd(Long storeListNo, String passwd);

	// ...은 컬럼이 너무 많으니까.
	/* select ... from boot_boardboard b1_0 where b1_0.title=? */
	StoreCommunity findByStoreTitle(String StoreTitle);

	/* findBy[FieldTitle]Containing */
	List<StoreCommunity> findByStoreTitleContaining(String storeTitle);
	

	List<StoreCommunity> findByNameContaining(String name);

	List<StoreCommunity> findByStoreContentContaining(String storeContent);

//	/* findBy[fieldName] [Between] */
	List<StoreCommunity> findByStoreCreateAtBetween(LocalDateTime storeCreateAt, LocalDateTime storeUpdateAt);

	/* findByOrderBy[FieldName] [Desc] */
	List<StoreCommunity> findByOrderByStoreListNoDesc();

	// 강제로 import 해줘?? 맞나..?
	List<StoreCommunity> findByOrderByStoreListNoDesc(Pageable pageable);

	Page<StoreCommunity> findByStoreListNoGreaterThan(long StoreListNo, Pageable pageable);

	/*
	 * JPQL 적용 JPA 에서 사용하는 객체지향 쿼리 언어이다. 데이터베이스 SQL 쿼리 언어와 유사하지만 테이블과 컬럼 이름 대신 매핑한
	 * 엔티티 이름과 속성 이름을 사용한다. 기본형식: SELECT 별칭 FROM 엔티티 이름 AS 별칭
	 
	@Query("SELECT b FROM StoreCommunity b order by b.storeListNo desc") // 정렬하여가져올거다. 조합해서 실행한다.
	public List<StoreCommunity> storeCommunityList();

	// ? 다음에 위치 값을 지정하는 위치 기준 파라미터를 사용.
	@Query("SELECT b FROM StoreCommunity b WHERE b.storeListNo = ?1")
	public StoreCommunity boardDetail(Long no);

	// JPQL통해서 얻는거.
	@Query("SELECT b FROM StoreCommunity b WHERE b.storeListNo = ?1 AND b.passwd = ?2")
	public StoreCommunity pwdConfirm(Long no, String passwd);

	@Query("SELECT b FROM StoreCommunity b")
	public Page<StoreCommunity> storeCommunityListPaging(Pageable pageable);

	Page<StoreCommunity> findByNoGreaterThan(long l, Pageable pageRequest);
	*/
}
