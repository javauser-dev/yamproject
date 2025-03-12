package com.yam.store.community.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yam.common.vo.PageRequestDTO;
import com.yam.common.vo.PageResponseDTO;
import com.yam.store.community.domain.StoreCommunity;
import com.yam.store.community.service.StoreCommunityService;

import lombok.RequiredArgsConstructor;

/************************************************************************* 
 * CORS 관련 설정
 * 현재 화면의 URL과 요청한 URL이 서로 다른 서버이기 때문에 CORS 정책에 따라 접근하지 못한다. 
 * 즉 CORS(Cross-Origin Resource Sharing, 교차 출처 리소스 공유)는 
 * 특정 서버에서 실행 중인 웹 애플리케이션이 다른 서버의 자원에 접근할 수 있는 권한을 부여하도록 
 * 브라우저에 알려주는 제약사항이다.
 * 
 * 동일 출처 정책(Same Origin Policy)은 브라우저가 보안상 이유로 스크립트에서 다른 서버로의 요청을 금지하는 정책이다. 
 * 악의적인 해커가 악성 스크립트를 이용하여 해커의 서버로 사용자 정보를 탈취할 가능성으로부터 보호하기 위한 관리적인 방법이다.
 *  
 * 서버가 같은 경우를 '동일 출처' , 다른 경우를 '교차 출처'라 한다. 
 * 적절한 CORS를 사용하면 동일 출처 정책을 지키면서 교차 출처 환경에서 요청을 가능하게 할 수 있다.
 * 
 * Ajax를 이용해서 서비스를 호출하게 되면 
 * 반드시 ‘교차 출처 리소스 공유(Cross-Origin Resource Sharing : CORS)’로 인해 정상적으로 호출이 제한된다. 
 * CORS 설정은 @Controller가 있는 클래스에 @CrossOrigin을 적용하거나 Spring Security를 이용하는 설정이 있다. 
 * @CrossOrigin 설정은 모든 컨트롤러에 개별적으로 적용해야 하고 
 * WebMvcConfigurer의 설정을 이용하면 전체 프로젝트에서 접근을 설정할 수 있다.
**************************************************************************/

@RestController
@RequestMapping("/api/storeCommunity/*")
@CrossOrigin
@RequiredArgsConstructor
public class StoreCommunityRestController {
	
	
	private final StoreCommunityService storeCommunityService;
	
	@GetMapping("/list")
	public PageResponseDTO<StoreCommunity> list(PageRequestDTO pageRequestDTO){
		return storeCommunityService.list(pageRequestDTO);
	}

}
