$(document).ready(function() {

	// 비밀번호 실시간 검증(keyup)
	$("#newPassword").keyup(function(){
	    let customerPassword = $(this).val().trim();
	    let passwordMsg =  $("#newPasswordError");
	    let passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/;

	    //빈 문자열이면 메시지 지우기.
	    if(customerPassword === ""){
	        passwordMsg.text("");
	        return;
	    }

	    if(!passwordRegex.test(customerPassword)){
	        passwordMsg.text("비밀번호는 영문자, 숫자, 특수문자를 각각 1개 이상 포함하여 8~15글자여야 합니다.");
	        passwordMsg.css("color", "red"); // 또는 다른 스타일

	        //return; // 추가: 유효하지 않으면 submit을 막음 (선택 사항)

	    } else{
	        passwordMsg.text("사용 가능한 비밀번호입니다.");
	        passwordMsg.css("color", "green"); // 또는 다른 스타일
	    }
	});
	
	// 닉네임 중복 검사 (blur 이벤트)
    $("#customerNickname").blur(function() {
        let newNickname = $(this).val().trim();
        let originalNickname = $("#originalNickname").val(); // 기존 닉네임
        let nicknameMsg = $("#customerNicknameError");
        let updateBtn = $("#nicknameUpdateBtn");

        // 입력값이 없으면 메시지 지우고 버튼 활성화 후 종료
        if (newNickname === "") {
            nicknameMsg.text("");
            updateBtn.prop("disabled", false);
            return;
        }

        // 기존 닉네임과 같으면 메시지 지우고 버튼 활성화 후 종료
        if (newNickname === originalNickname) {
            nicknameMsg.text("");
            updateBtn.prop("disabled", false);
            return;
        }

        // 닉네임 형식 검사
        let nicknameRegex = /^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,8}$/;
        if (!nicknameRegex.test(newNickname)) {
            nicknameMsg.text("닉네임은 2~8글자의 영문, 숫자, 한글로 구성되어야 합니다.");
            updateBtn.prop("disabled", true); // 형식 오류 시 비활성화
            return;
        }

        $.ajax({
            url: '/customer/checkNickname',
            type: 'POST',
            data: {
                customerNickname: newNickname,
                currentCustomerId: $("#customerId").val()
            },
            success: function(response) {
                if (response === "available") {
                    nicknameMsg.text("사용 가능한 닉네임입니다.");
                    nicknameMsg.css("color", "green");
                    updateBtn.prop("disabled", false);
                } else { // "duplicated"
                    nicknameMsg.text("이미 사용 중인 닉네임입니다.");
                    nicknameMsg.css("color", "red");
                    updateBtn.prop("disabled", true);
                }
            },
            error: function() {
                nicknameMsg.text("서버와의 통신 중 오류가 발생했습니다.");
                nicknameMsg.css("color", "red");
                updateBtn.prop("disabled", true); // 서버 오류 시에도 비활성화
            }
        });
    });

    // 닉네임 변경 버튼 클릭 이벤트
    $("#nicknameUpdateBtn").click(function(event) {
        event.preventDefault(); // 1. 폼 제출을 항상 막음

        let newNickname = $("#customerNickname").val().trim();
        let originalNickname = $("#originalNickname").val();
        let nicknameMsg = $("#customerNicknameError");

        // 2. 닉네임 중복 검사 다시 수행 (최종 확인)
        $.ajax({
            url: '/customer/checkNickname',
            type: 'POST',
            data: {
                customerNickname: newNickname,
                currentCustomerId: $("#customerId").val()
            },
            success: function(response) {
                if (response === "available") {
                    // 3. 사용 가능하면 폼 제출
                    $("#updateNicknameForm")[0].submit(); // 프로그래밍 방식으로 폼 제출
                } else { // "duplicated"
                    // 4. 중복이면 알림 표시, 폼 제출 X
                    nicknameMsg.text("이미 사용 중인 닉네임입니다.");
                    nicknameMsg.css("color", "red");
                }
            },
            error: function() {
                nicknameMsg.text("서버와의 통신 중 오류가 발생했습니다.");
                 nicknameMsg.css("color", "red");
            }
        });
    });

    // 이메일 인증 번호 보내기 (AJAX)
    $("#sendVerificationCodeBtn").click(function() {
        let customerEmail = $("#customerEmail").val().trim();

        // 이메일 주소 형식 검사 (클라이언트 측)
        let emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        if (!emailRegex.test(customerEmail)) {
            $("#customerEmailError").text("유효한 이메일 주소를 입력하세요.").css("color", "red");
            return;
        } else{
            $("#customerEmailError").text(""); //에러 메시지 지움.
        }

         // 기존 이메일과 동일한지 확인
        let originalEmail = $("#originalEmail").val(); //input type="hidden"
        if(customerEmail === originalEmail) {
              $("#customerEmailError").text("기존 이메일과 동일합니다.").css("color", "red");
              return;
        } else {
           $("#customerEmailError").text(""); //에러 메시지 지움.
        }

        // 이메일 중복 검사 (AJAX)
        $.ajax({
            url: '/customer/checkEmail',
            type: 'POST',
            data: { customerEmail: customerEmail },
            success: function(response) {
                if (response.isAvailable) { // 이메일 사용 가능
                    // 인증 번호 전송 AJAX 요청
                    $.ajax({
                        url: '/customer/sendVerificationCode',
                        type: 'POST',
                        data: { customerEmail: customerEmail },
                        success: function(data) {
                            alert("인증번호가 발송되었습니다.");
                            $("#verificationCodeDiv").show(); // 인증 번호 입력 필드 보이기
                        },
                        error: function() {
                            alert("인증번호 발송 중 오류가 발생했습니다.");
                        }
                    });
                } else { // 이메일 이미 사용 중
                    $("#customerEmailError").text("이미 사용 중인 이메일입니다.").css("color", "red");
                }
            },
            error: function() {
                $("#customerEmailError").text("서버와의 통신 중 오류가 발생했습니다.").css("color", "red");
            }
        });
    });

      // 인증 번호 확인 (AJAX)
    $("#verifyCodeBtn").click(function() {
        let code = $("#verificationCode").val().trim();
        $.ajax({
            url: '/customer/verifyCode',
            type: 'POST',
            data: { code: code },
            success: function(data) {
                if (data.verified) {
                    alert("인증되었습니다.");
                    $("#verificationCodeDiv").hide(); // 인증 번호 입력 필드 숨기기
                    $("#updateEmailBtn").show(); // 이메일 변경 버튼 보이기.

                } else {
                    alert("인증번호가 일치하지 않습니다.");
                    $("#verificationCodeError").text("인증번호가 일치하지 않습니다.").css("color", "red"); // 에러 메시지
                }
            },
            error: function() {
                alert("인증 처리 중 오류가 발생했습니다.");
                $("#verificationCodeError").text("인증 처리 중 오류가 발생했습니다.").css("color", "red"); // 에러 메시지
            }
        });
    });
});