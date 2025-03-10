package com.yam.store.community.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yam.store.community.repository.StoreCommunityRepository;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class StoreCommunityRepositoryTests {
	
	@Setter(onMethod_ = @Autowired)
	private StoreCommunityRepository storeCommunityRepository;

	/* 게시판 등록 - save(): 주어진 엔티티를 저장 */
	@Test
	public void storeCommunityInsertTest() {
		StoreCommunity storeCommunity = new StoreCommunity();
		storeCommunity.setStoreCategory("가입인사");
		storeCommunity.setName("늘한봄"); //사장님
		storeCommunity.setStoreTitle("노력 명언");
		storeCommunity.setStoreContent("우리 인생은 우리들이 노력한만큼 가치가 있다.");
		storeCommunity.setPasswd("1234");
		storeCommunity.setStoreCreateAt(LocalDateTime.now());

		log.info("### storeCommunity 테이블에 첫번째 데이터 입력");
		storeCommunityRepository.save(storeCommunity); // 보드레포지토리에 보드를 저장해라.

		StoreCommunity storeCommunity1 = new StoreCommunity();
		storeCommunity1.setStoreCategory("고민상담");
		storeCommunity1.setName("홍길동");
		storeCommunity1.setStoreTitle("끈기 명언");
		storeCommunity1.setStoreContent("실패한 자가 패배하는 것이 아니라 포기한 자가 패배하는 것이다.");
		storeCommunity1.setPasswd("1234");
		storeCommunity1.setStoreCreateAt(LocalDateTime.now());

		log.info("### storeCommunity 테이블에 두번째 데이터 입력");
		storeCommunityRepository.save(storeCommunity1);

		StoreCommunity storeCommunity2 = new StoreCommunity();
		storeCommunity2.setStoreCategory("정보공유");
		storeCommunity2.setName("강희수");
		storeCommunity2.setStoreTitle("끈기 명언");
		storeCommunity2.setStoreContent("단 한번의 노력으로 자기의 바람을 성취할 수 없다. 또한 단 한번의 실패로 그 소망을 모두 포기할 수도 없는 것이다.");
		storeCommunity2.setPasswd("1234");
		storeCommunity2.setStoreCreateAt(LocalDateTime.now());

		log.info("### storeCommunity 테이블에 세번째 데이터 입력");
		storeCommunityRepository.save(storeCommunity2);

		StoreCommunity storeCommunity3 = new StoreCommunity();
		storeCommunity3.setStoreCategory("정보공유");
		storeCommunity3.setName("강희수");
		storeCommunity3.setStoreTitle("언어 명언");
		storeCommunity3.setStoreContent("말이 입힌 상처는 칼이 입힌 상처보다 깊다.");
		storeCommunity3.setPasswd("1234");

		log.info("### board 테이블에 네번째 데이터 입력");
		storeCommunityRepository.save(storeCommunity3);
	}

	/* 게시판 전체 레코드 수 구하기 - count(): 사용가능한 엔티티 수를 반환 */
//		@Test
	public void storeCommunityCountTest() {
		long storeCommunityCount = storeCommunityRepository.count();
		log.info(String.valueOf(storeCommunityCount));
	}

	/* 게시판 리스트 - findAll(): T타입의 모든 인스턴스를 반환. */
//	@Test
	public void storeCommunityListTest() {
		List<StoreCommunity> storeCommunityList = (List<StoreCommunity>) storeCommunityRepository.findAll();
		for (StoreCommunity storeCommunity : storeCommunityList) {
			log.info(storeCommunity.toString());
		}
	}

	/* 게시판 상세 조회 - findById(ID id): ID로 엔티티를 검색. */
//	@Test
	public void storeCommunityDetailTest() {
		Optional<StoreCommunity> storeCommunityOptional = storeCommunityRepository.findById(2L);
		// isPresent() 메소드를 사용하여 Optional 객체에 저장된 값이 null 인지 아닌지를 먼저 확인
		if (storeCommunityOptional.isPresent()) {
			StoreCommunity storeCommunity = storeCommunityOptional.get();
			log.info(storeCommunity.toString());
		}

	}

	/* 게시판 수정 - findById() 메서드로 데이터를 가져와서 변경할 필드만 설정해 주면 된다. */
//	@Test
	public void storeCommunityUpdateTest() {
		Optional<StoreCommunity> storeCommunityOptional = storeCommunityRepository.findById(3L);

		if (storeCommunityOptional.isPresent()) {
			StoreCommunity storeCommunity = storeCommunityOptional.get();
			storeCommunity.setStoreTitle("힘들때 힘이 되는 명언");
			storeCommunity.setStoreContent("조급해 하지 말고 조바심내지 말고, 할 수 있는 만큼 최선을 다하자.");
			storeCommunity.setPasswd("1111");

			log.info("### board 테이블에 데이터 수정");
			storeCommunityRepository.save(storeCommunity);
		}
	}

	/* 게시판 삭제 - deleteById(ID id) : 주어진 ID를 가진 엔티티를 삭제 */
//	@Test
	public void storeCommunityDeleteTest() {
		storeCommunityRepository.deleteById(3L);
	}

	/* 게시판 본인 글 여부 확인 */
//	@Test
	public void pwdConfirmTest() {
		StoreCommunity storeCommunity = storeCommunityRepository.findByStoreListNoAndPasswd(10L, "1234");
		log.info("데이터: " + storeCommunity.toString());
	}

	/* 게시판 제목, 이름, 내용 검색 */
//	@Test
	public void findByStoreTitleContainingTest() {
//		 StoreCommunity titleSearch = storeCommunityRepository.findByStoreTitle("노력 명언");
//		 log.info(titleSearch.toString());

		// 하나씩 해보기.
		// 제목검색
//		List<StoreCommunity> list = storeCommunityRepository.findByStoreTitleContaining("인생");
//		 log.info(list.toString());
		
		// 이름검색
//		 List<StoreCommunity> list = storeCommunityRepository.findByNameContaining("희수");
	
		// 내용검색
//		 List<StoreCommunity> list = storeCommunityRepository.findByStoreContentContaining("인생");
//		 log.info(list.toString());
		
		// 등록일검색
//		 log.info(LocalDateTime.now().minusDays(1).toString()); // 지금을 기준으로 어제
//		 log.info(LocalDateTime.now().toString()); //지금~
		
		// 이거는 테스트 성공 못했음. 250303 22:00 성공함 
		 List<StoreCommunity> list = storeCommunityRepository.findByStoreCreateAtBetween(LocalDateTime.now().minusDays(1),LocalDateTime.now());
		 log.info(String.valueOf(list.size()));
		 for (StoreCommunity storeCommunity : list) {
			log.info(storeCommunity.toString());
		}
	}

	/* 게시판 번호 순 내림차순 정렬 - 정렬조건(OrderByNoDesc)이 검색조건에 추가되고 리스트(List) 형태로 조회결과를 반환. */
//	@Test
	public void findByOrderByNoDescTest() {
		// findByOrderByNoDesc(): 내림차순으로 번호 정력
		List<StoreCommunity> storeCommunityList = storeCommunityRepository.findByOrderByStoreListNoDesc();
		for (StoreCommunity storeCommunity : storeCommunityList) {
			log.info(storeCommunity.toString());
		}

	}

//	@Test
	public void findByStoreListNoGreaterThanTest() {
		Pageable pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "StoreListNo");
		Page<StoreCommunity> page = storeCommunityRepository.findByStoreListNoGreaterThan(0L, pageRequest);
		int totalPages = page.getTotalPages();

		log.info(page.toString());
		log.info("totalPages : " + totalPages);

		Pageable pageable = page.getPageable();

		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		log.info(pageable.toString());
		log.info("pageNumber : " + pageNumber);
		log.info("pageSize : " + pageSize);

		List<StoreCommunity> storeCommunityList = page.getContent();
		for (StoreCommunity storeCommunity : storeCommunityList) {
			log.info(storeCommunity.toString());
		}

	}

//	@Test
	public void storeCommunityAllInsertTest() {
		for (int i = 1; i <= 100; i++) {
			StoreCommunity storeCommunity = StoreCommunity.builder().storeTitle("Title..." + i).name("홍길동" + i).storeCategory("가입인사")
					.storeContent("실패한 자가 패배하는 것이 아니라 포기한 자가 패배하는 것이다.").passwd("1234").storeCreateAt(LocalDateTime.now()).build();
			storeCommunityRepository.save(storeCommunity);
		}
	}

}
