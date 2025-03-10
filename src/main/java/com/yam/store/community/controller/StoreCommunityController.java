package com.yam.store.community.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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
import com.yam.store.community.domain.StoreCommunity;
import com.yam.store.community.service.StoreCommunityService;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/storeCommunity/*")
@RequiredArgsConstructor
public class StoreCommunityController {  // 컨트롤는 마이바티스와 비슷하다.

	private final StoreCommunityService storeCommunityService;
	private final CustomFileUtil fileUtil;

	/*
	 * 검색기능 및 페이징 처리 예외
	 * @GetMapping("/boardList") private String boardList(Board board, Model model)
	 * { List<Board> boardList = boardService.boardList(board);
	 * model.addAttribute("boardList", boardList); return "client/board/boardList";
	 * }
	 */

	/**
	 * 검색 기능 및 페이징 처리 제외
	 * @param board
	 * @return
	 
	@GetMapping("/boardList")
	public String boardList(Board board, Model model) {
		List<Board> boardList = boardService.boardList(board);
		model.addAttribute("boardList", boardList);
		
		return "client/board/boardList";
	}*/

	/* 업로드 파일 보여주기 */
	/**/
	@ResponseBody
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){
		return fileUtil.getFile(fileName);
	}
	
	/* 페이징 처리까지 된 애*/
	@GetMapping("/storeCommunityList")
	public String storeCommunityList(StoreCommunity storeCommunity, PageRequestDTO pageRequestDTO, Model model) {
		PageResponseDTO<StoreCommunity> storeCommunityList = storeCommunityService.list(pageRequestDTO);
		model.addAttribute("storeCommunityList", storeCommunityList);

		return "client/storeCommunity/storeCommunityList";
	}
	/**/
	@GetMapping("/insertForm")
	public String insertForm(StoreCommunity storeCommunity) {
		return "client/storeCommunity/insertForm";
	}
	/**/
	@PostMapping("/boardInsert")
	public String boardInsert(StoreCommunity storeCommunity) {
		if (!storeCommunity.getFile().isEmpty()) { //새로 업로드 파일이 존재하면
			String uploadFileName = fileUtil.saveFile(storeCommunity.getFile());
			storeCommunity.setFilename(uploadFileName);
		}

		storeCommunityService.storeCommunityInsert(storeCommunity);
		return "redirect:/storeCommunity/storeCommunityList";
	}
//	/**/
	@GetMapping("/{storeListNo}")
	public String boardDetail(@PathVariable Long storeListNo, StoreCommunity storeCommunity, Model model) {
		storeCommunity.setStoreListNo(storeListNo);
		StoreCommunity detail = storeCommunityService.storeCommunityDetail(storeCommunity);
		model.addAttribute("detail", detail);

//		/*
//		 * String에서 줄바꿈(newline)은 Window에서 \r\n, Linux에서 \n으로 표현된다. 하지만 이런 방식은, 서로 다른
//		 * 종류의 OS에서 동작하는 프로그램에서 문제가 발생할 수 있다. [참고]String#format()의 %n: String#format()에서
//		 * %n은 line separator를 의미한다.*/
		 
		String newLine = System.getProperty("line.separator").toString();
		model.addAttribute("newLine", newLine);

		return "client/storeCommunity/storeCommunityDetail";
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
		
		if(!storeCommunity.getFile().isEmpty()) {
			if(updateData.getFilename()!= null) {
				fileUtil.deleteFile(updateData.getFilename());
			}
			
			String uploadFileName= fileUtil.saveFile(storeCommunity.getFile());
			storeCommunity.setFilename(uploadFileName);
		}
		
		storeCommunityService.storeCommunityUpdate(storeCommunity);
		return "redirect:/storeCommunity/" + storeCommunity.getStoreListNo();
	}
//	/**/
	@PostMapping("/boardDelete")
	public String storeCommunityDelete(StoreCommunity storeCommunity) {
		StoreCommunity deleteData = storeCommunityService.getStoreCommunity(storeCommunity.getStoreListNo());
		if(deleteData.getFilename()!= null) { //기존파일이 존재하면
			fileUtil.deleteFile(deleteData.getFilename());
		}
		storeCommunityService.storeCommunityDelete(storeCommunity);
		return "redirect:/storeCommunity/storeCommunityList";
	}
}