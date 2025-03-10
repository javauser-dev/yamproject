package com.yam.config.oauth;

import java.time.LocalDate;
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
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 암호화

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver" 또는 "kakao"

		if ("naver".equals(registrationId)) {
			return processNaverLogin(oAuth2User);
		} else if ("kakao".equals(registrationId)) {
			return processKakaoLogin(oAuth2User);
		}

		return oAuth2User;
	}

	// ✅ 네이버 로그인 처리
	private OAuth2User processNaverLogin(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// 🔹 네이버 응답 데이터 구조 확인 후 response 키 체크
		if (attributes.get("response") == null) {
			throw new OAuth2AuthenticationException("네이버 로그인 응답 데이터에 'response' 키가 없습니다.");
		}

		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		// 🔹 값이 존재하는지 확인하고 가져오기 (null이면 기본값 또는 예외 처리)
		String naverId = response.get("id") != null ? response.get("id").toString() : "";
		String nickname = response.get("nickname") != null ? response.get("nickname").toString() : "네이버 사용자";
		String email = response.get("email") != null ? response.get("email").toString() : "";
		String name = response.get("name") != null ? response.get("name").toString() : "네이버 사용자"; // ✅ 기본값 설정
		String birthYear = response.get("birthyear") != null ? response.get("birthyear").toString() : "2000";
		String birthDay = response.get("birthday") != null ? response.get("birthday").toString() : "01-01";
		String gender = response.get("gender") != null ? response.get("gender").toString() : "N";

		// 🔹 생년월일을 YYYY-MM-DD 형식으로 변환
		String birthDateStr = birthYear + "-" + birthDay;

		// 🔹 비밀번호를 랜덤 문자열로 설정 (네이버는 비밀번호를 제공하지 않음)
		String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

		// ✅ 기존 회원 조회 (네이버 ID 기반)
		Optional<Member> existingMember = memberRepository.findByCustomerId(naverId);

		if (existingMember.isEmpty()) {
			// 신규 회원 가입 처리
			Member newMember = new Member();
			newMember.setCustomerId(naverId);
			newMember.setCustomerNickname(nickname);
			newMember.setCustomerName(name); // ✅ 이름 저장
			newMember.setCustomerEmail(email);
			newMember.setCustomerGender(gender); // ✅ 성별 저장
			newMember.setCustomerBirthDate(LocalDate.parse(birthDateStr)); // ✅ 생년월일 저장
			newMember.setCustomerPassword(randomPassword); // ✅ 비밀번호 설정
			newMember.setCustomerApproval("Y"); // ✅ 기본 승인 상태 설정
			newMember.setCustomerRole("ROLE_CUSTOMER");

			memberRepository.save(newMember);
		}

		return new CustomOAuth2User(oAuth2User, "ROLE_CUSTOMER"); // ✅ 권한 적용
	}

	// ✅ 카카오 로그인 처리
	private OAuth2User processKakaoLogin(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		String kakaoId = attributes.get("id").toString();
		String nickname = profile.get("nickname").toString();
		String email = kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : "";
		String name = nickname; // 카카오는 별도로 이름을 제공하지 않으므로 닉네임을 이름으로 사용

		// 🔹 비밀번호를 랜덤 문자열로 설정 (카카오는 비밀번호를 제공하지 않음)
		String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

		// ✅ 기존 회원 조회 (카카오 ID 기반)
		Optional<Member> existingMember = memberRepository.findByCustomerId(kakaoId);

		if (existingMember.isEmpty()) {
			// 신규 회원 가입 처리
			Member newMember = new Member();
			newMember.setCustomerId(kakaoId);
			newMember.setCustomerNickname(nickname);
			newMember.setCustomerName(name); // ✅ 이름 저장
			newMember.setCustomerEmail(email);
			newMember.setCustomerGender("N"); // 기본값 설정
			newMember.setCustomerBirthDate(LocalDate.of(2000, 1, 1)); // 기본값 설정
			newMember.setCustomerPassword(randomPassword); // ✅ 비밀번호 설정
			newMember.setCustomerApproval("Y"); // 기본 승인 상태 설정
			newMember.setCustomerRole("ROLE_CUSTOMER");

			memberRepository.save(newMember);
		}

		return oAuth2User;
	}
}
