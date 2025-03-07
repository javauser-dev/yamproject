package com.yam.customer.member.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.WithdrawnMember;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.member.repository.WithdrawnMemberRepository;
import com.yam.customer.member.vo.MemberSignupRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final WithdrawnMemberRepository withdrawnMemberRepository;
	private LocalDateTime lastDeletionCheckTime = LocalDateTime.now(); // 마지막으로 확인한 시간을 기록

	@Transactional
	public void signup(MemberSignupRequest request) {
		// MemberSignupRequest -> Member 엔티티로 데이터 복사
		Member member = new Member();
		member.setCustomerId(request.getCustomerId());
		// 비밀번호 암호화
		member.setCustomerPassword(passwordEncoder.encode(request.getCustomerPassword()));
		member.setCustomerNickname(request.getCustomerNickname());
		member.setCustomerName(request.getCustomerName());
		member.setCustomerEmail(request.getCustomerEmail());
		member.setCustomerBirthDate(request.getCustomerBirthDate());
		member.setCustomerGender(request.getCustomerGender());
		member.setCustomerApproval("Y");
		member.setCustomerCreateDate(LocalDateTime.now()); // 🔥 직접 설정

		memberRepository.save(member);
		memberRepository.flush(); // 🔥 즉시 반영
	}

	public boolean isCustomerIdDuplicated(String customerId) {
		try {
			return memberRepository.existsById(customerId);
		} catch (Exception e) {
			// 예외 처리 (로그 기록, 사용자 정의 예외 발생 등)
			System.err.println("ID 중복 검사 중 오류 발생: " + e.getMessage());
			throw new RuntimeException("ID 중복 검사 중 오류 발생", e); // 더 나은 방법
		}
	}

	public boolean isCustomerNicknameDuplicated(String customerNickname, String currentCustomerId) {
		Member member = memberRepository.findByCustomerNickname(customerNickname); // Optional 사용 안 함
		if (member != null) {
			// 닉네임이 이미 존재
			if (currentCustomerId != null) { // myInfo에서 닉네임 중복 검사
				return !member.getCustomerId().equals(currentCustomerId); // 현재 사용자의 ID와 비교
			} else {// Signup에서 닉네임 중복 검사.
				return true;
			}
		}
		return false; // 닉네임 사용 가능
	}

	public Member getMemberById(String customerId) {
		return memberRepository.findById(customerId).orElse(null);
	}

	public void updatePassword(String customerId, String newPassword) {
		Member member = memberRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

		// 비밀번호 암호화 (새 비밀번호가 비어있지 않은 경우)
		if (newPassword != null && !newPassword.isEmpty()) {
			member.setCustomerPassword(passwordEncoder.encode(newPassword));
		}
		// 변경된 내용은 트랜잭션 내에서 자동으로 저장됨 (save 호출 불필요)
	}

	public void updateNickname(String customerId, String newNickname) {
		Member member = memberRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

		member.setCustomerNickname(newNickname);
		// memberRepository.save(member); // @Transactional에 의해 자동 저장
	}

	public void updateEmail(String customerId, String newEmail) {
		Member member = memberRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

		member.setCustomerEmail(newEmail);
	}

	public void updateProfileImage(String customerId, String imageUrl) {
		Member member = memberRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

		member.setCustomerProfileImage(imageUrl);
		// @Transactional에 의해 자동 저장
	}

	// 회원 탈퇴: customer_manage -> withdrawn_customer 이동
	public void moveToWithdrawn(String customerId, String withdrawalReason) {
		Member member = memberRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId));

		// WithdrawnMember 엔티티 생성 및 데이터 복사
		WithdrawnMember withdrawnMember = new WithdrawnMember();
		withdrawnMember.setCustomerId(member.getCustomerId());
		withdrawnMember.setCustomerPassword(member.getCustomerPassword());
		withdrawnMember.setCustomerNickname(member.getCustomerNickname());
		withdrawnMember.setCustomerName(member.getCustomerName());
		withdrawnMember.setCustomerEmail(member.getCustomerEmail());
		withdrawnMember.setCustomerBirthDate(member.getCustomerBirthDate());
		withdrawnMember.setCustomerGender(member.getCustomerGender());
		withdrawnMember.setCustomerApproval(member.getCustomerApproval());
		withdrawnMember.setCustomerProfileImage(member.getCustomerProfileImage());
		withdrawnMember.setCustomerCreateDate(member.getCustomerCreateDate());

		// 탈퇴 관련 정보 설정
		withdrawnMember.setWithdrawalRequestedAt(LocalDateTime.now());
		withdrawnMember.setWithdrawalCompletedAt(LocalDateTime.now().plus(5, ChronoUnit.YEARS));
		withdrawnMember.setWithdrawalReason(withdrawalReason);

		withdrawnMemberRepository.save(withdrawnMember);
	}

	// 회원 탈퇴: customer_manage에서 삭제
	public void deleteMember(String customerId) {
		if (!memberRepository.existsById(customerId)) {
			throw new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + customerId);
		}
		memberRepository.deleteById(customerId);
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정
	public void deleteExpiredWithdrawnMembers() {
		LocalDateTime now = LocalDateTime.now();

		// 서버가 꺼져있다가 켜진 경우, 마지막 확인 이후의 모든 시간 범위를 커버
		if (now.isAfter(lastDeletionCheckTime.plusDays(1))) { // 하루 이상 지났으면
			// lastDeletionCheckTime부터 now 사이의 완료된 회원 모두 삭제
			withdrawnMemberRepository.deleteByWithdrawalCompletedAtBetween(lastDeletionCheckTime, now);
		} else {
			// 일반적인 경우 (매일 자정에 실행되는 경우)
			withdrawnMemberRepository.deleteByWithdrawalCompletedAtBefore(now);
		}

		lastDeletionCheckTime = now; // 마지막 확인 시간 업데이트
	}
}