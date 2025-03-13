package com.yam.config.oauth;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		if ("naver".equals(registrationId)) {
			return processNaverLogin(oAuth2User);
		}
		return oAuth2User;
	}

	private OAuth2User processNaverLogin(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();

		if (attributes.get("response") == null) {
			throw new OAuth2AuthenticationException("네이버 로그인 응답 데이터 오류");
		}

		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		String naverId = response.get("id").toString();
		String email = response.get("email") != null ? response.get("email").toString() : null;
		String name = response.get("name") != null ? response.get("name").toString() : "알 수 없음";
		String birthYear = response.get("birthyear") != null ? response.get("birthyear").toString() : "0000";
		String birthDay = response.get("birthday") != null ? response.get("birthday").toString() : "01-01";
		String birthDateStr = birthYear + "-" + birthDay;
		String gender = response.get("gender") != null ? response.get("gender").toString() : "N";

		// **[1] 이메일이 없을 경우 임시 이메일 생성**
		if (email == null || email.isEmpty()) {
			email = UUID.randomUUID().toString() + "@temporary.com"; // UUID 기반 임시 이메일
		}

		Optional<Member> existingMember = memberRepository.findByCustomerId(naverId);

		if (existingMember.isEmpty()) {
			// **[2] 신규 회원 처리**
			throw new OAuth2AuthenticationException(
					"NAVER_NEW_USER:" + email + ":" + name + ":" + birthDateStr + ":true");
		}

		return new CustomOAuth2User(oAuth2User, "ROLE_CUSTOMER");
	}
}
