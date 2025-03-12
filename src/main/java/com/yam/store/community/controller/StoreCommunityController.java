package com.yam.store.community.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yam.common.util.CustomFileUtil;
import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.store.Store;
import com.yam.store.community.domain.StoreCommunity;
import com.yam.store.community.service.StoreCommunityService;
import com.yam.store.service.StoreService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/storeCommunity/*")
@RequiredArgsConstructor
public class StoreCommunityController { // 컨트롤는 마이바티스와 비슷하다.

	private final StoreCommunityService storeCommunityService;
	private final CustomFileUtil fileUtil;
	private final StoreService storeService;

	/*
	 * 검색기능 및 페이징 처리 예외
	 * 
	 * @GetMapping("/boardList") private String boardList(Board board, Model model)
	 * { List<Board> boardList = boardService.boardList(board);
	 * model.addAttribute("boardList", boardList); return "client/board/boardList";
	 * }
	 */

	/**
	 * 검색 기능 및 페이징 처리 제외
	 * 
	 * @param board
	 * @return
	 * 
	 *         @GetMapping("/boardList") public String boardList(Board board, Model
	 *         model) { List<Board> boardList = boardService.boardList(board);
	 *         model.addAttribute("boardList", boardList);
	 * 
	 *         return "client/board/boardList"; }
	 */

	/* 업로드 파일 보여주기 */
	/**/
	@ResponseBody
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}

	/* 페이징 처리까지 된 애 */
	@GetMapping("/storeCommunityList")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STORE')")
	public String storeCommunityList(StoreCommunity storeCommunity, PageRequestDTO pageRequestDTO, Model model) {
		PageResponseDTO<StoreCommunity> storeCommunityList = storeCommunityService.list(pageRequestDTO);
		model.addAttribute("storeCommunityList", storeCommunityList);

		return "client/storeCommunity/storeCommunityList";
	}

	@GetMapping("/insertForm")
	public String insertForm(HttpSession session, Model model) {
		StoreCommunity storeCommunity = new StoreCommunity();

		// ✅ 세션에서 로그인된 Store 정보 가져오기
		Store store = (Store) session.getAttribute("loggedInStore");

		if (store == null) {
			System.out.println("로그인된 store가 없습니다. 로그인 페이지로 이동합니다.");
			return "redirect:/login";
		}

		System.out.println("✅ 현재 로그인한 사업자의 닉네임: " + store.getStoreNickname());

		// ✅ StoreCommunity의 name 필드에 storeNickname 설정
		storeCommunity.setName(store.getStoreNickname());

		model.addAttribute("storeCommunity", storeCommunity);
		return "client/storeCommunity/insertForm";
	}

	/**/
	@PostMapping("/boardInsert")
	public String boardInsert(StoreCommunity storeCommunity, HttpSession session) {
		// ✅ 세션에서 로그인된 Store 객체 가져오기
		Store store = (Store) session.getAttribute("loggedInStore");

		if (store == null) {
			System.out.println("로그인된 store가 없습니다. 로그인 페이지로 이동합니다.");
			return "redirect:/login";
		}

		// ✅ storeNickname 설정
		storeCommunity.setName(store.getStoreNickname());

		// ✅ 파일 업로드 처리
		if (!storeCommunity.getFile().isEmpty()) {
			String uploadFileName = fileUtil.saveFile(storeCommunity.getFile());
			storeCommunity.setFilename(uploadFileName);
		}

		storeCommunityService.storeCommunityInsert(storeCommunity);
		return "redirect:/storeCommunity/storeCommunityList";
	}

//	/**/
	@PostMapping("/updateForm")
	public String updateForm(StoreCommunity storeCommunity, Model model) {
		StoreCommunity updateData = storeCommunityService.getStoreCommunity(storeCommunity.getStoreListNo());
		model.addAttribute("updateData", updateData);
		return "client/storeCommunity/updateForm";
	}

//	/**/
	@PostMapping("/boardUpdate")
	public String boardUpdate(StoreCommunity storeCommunity) {
		StoreCommunity updateData = storeCommunityService.getStoreCommunity(storeCommunity.getStoreListNo());

		if (!storeCommunity.getFile().isEmpty()) {
			if (updateData.getFilename() != null) {
				fileUtil.deleteFile(updateData.getFilename());
			}

			String uploadFileName = fileUtil.saveFile(storeCommunity.getFile());
			storeCommunity.setFilename(uploadFileName);
		}

		storeCommunityService.storeCommunityUpdate(storeCommunity);
		return "redirect:/storeCommunity/" + storeCommunity.getStoreListNo();
	}

//	/**/
	@PostMapping("/boardDelete")
	public String storeCommunityDelete(StoreCommunity storeCommunity) {
		StoreCommunity deleteData = storeCommunityService.getStoreCommunity(storeCommunity.getStoreListNo());
		if (deleteData.getFilename() != null) { // 기존파일이 존재하면
			fileUtil.deleteFile(deleteData.getFilename());
		}
		storeCommunityService.storeCommunityDelete(storeCommunity);
		return "redirect:/storeCommunity/storeCommunityList";
	}

	// ✅ 권한이 없는 사용자를 메인 페이지로 리다이렉트
	@GetMapping("/accessDenied")
	public String accessDenied(Model model) {
		model.addAttribute("errorMessage", "사업자 전용 게시판입니다.");
		return "redirect:/";
	}

	@GetMapping("/{storeListNo}")
	public String boardDetail(@PathVariable("storeListNo") Long storeListNo, Model model) {
		// 게시글 상세 정보를 가져옴
		StoreCommunity detail = storeCommunityService.getStoreCommunity(storeListNo);

		if (detail == null) {
			System.out.println("❌ 해당 게시글을 찾을 수 없습니다. storeListNo: " + storeListNo);
			return "redirect:/storeCommunity/storeCommunityList";
		}

		System.out.println("✅ 게시글 상세 조회 - 제목: " + detail.getStoreTitle());

		// 모델에 게시글 정보 추가
		model.addAttribute("storeCommunity", detail);

		// 상세 페이지로 이동
		return "client/storeCommunity/storeCommunityDetail";
	}
}