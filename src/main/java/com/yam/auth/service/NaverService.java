package com.yam.auth.service;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.yam.auth.dto.NaverUser;

@Service
public class NaverService {

	public NaverUser getUserInfo(String accessToken) {
		String apiUrl = "https://openapi.naver.com/v1/nid/me";

		try {
			RestTemplate restTemplate = new RestTemplate();

			// ✅ 헤더에 Authorization 추가 (Bearer {accessToken})
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			// ✅ GET 요청을 보냄
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

			// ✅ API 응답 출력 (디버깅용)
			System.out.println("✅ 네이버 API 응답: " + response.getBody());

			// ✅ JSON 응답 파싱
			JSONObject jsonResponse = new JSONObject(response.getBody());
			if (!jsonResponse.has("response")) {
				System.out.println("❌ 네이버 API 응답 오류: response 필드가 없음!");
				return null;
			}

			JSONObject responseObj = jsonResponse.getJSONObject("response");

			String name = responseObj.optString("name", "");
			String email = responseObj.optString("email", "");
			String birthYear = responseObj.optString("birthyear", "");
			String birthday = responseObj.optString("birthday", "");
			String birthDate = birthYear.isEmpty() || birthday.isEmpty() ? "" : birthYear + "-" + birthday;

			return new NaverUser(name, email, birthDate);
		} catch (Exception e) {
			System.out.println("❌ 네이버 API 호출 중 오류 발생: " + e.getMessage());
			return null;
		}
	}
}
