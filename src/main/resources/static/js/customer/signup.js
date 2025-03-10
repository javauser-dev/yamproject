$(document).ready(function() {

    // 입력 값 형식 검사 함수
    function validateInput(inputId, regex, errorMessage, errorSpan) {
        let value = $(inputId).val().trim();

        if (value === "") {
            $(errorSpan).text("필수 입력 항목입니다.");
            return false;
        }

        if (!regex.test(value)) {
            $(errorSpan).text(errorMessage);
            return false;
        }

        $(errorSpan).text(""); // 에러 메시지 지우기
        return true;
    }

    // 아이디 중복 검사 (blur 이벤트에서만)
    $("#customerId").blur(function() {
        let customerId = $(this).val().trim();
         // 빈 문자열이거나, 형식에 맞지 않으면 중복 검사 X
        if (customerId === "" || !/^[a-zA-Z0-9]{2,8}$/.test(customerId)) {
           return;
        }

        $.ajax({
            url: "/customer/checkId",
            type: "GET",
            data: { customerId: customerId },
            success: function(response) {
                if (response === "duplicated") {
                    $("#customerIdError").text("중복된 아이디입니다.");
                    $("#customerId").val(""); // 입력 값 지우기
                } else {
                    $("#customerIdError").text("");
                }
            },
            error: function() {
                $("#customerIdError").text("서버와의 통신 중 오류가 발생했습니다."); // alert 대신 에러 메시지
            }
        });
    });

	// 닉네임 중복 검사 (blur 이벤트) - 회원 가입 페이지
    $("#customerNickname").blur(function() {
        let customerNickname = $(this).val().trim();
        if (customerNickname === "" || !/^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,8}$/.test(customerNickname)) {
            return;
        }

        let data = { customerNickname: customerNickname }; // data 객체 생성

        $.ajax({
            url: "/customer/signCheckNickname", // 변경된 URL
            type: "GET",
            data: data,
            success: function(response) {
                if (response === "duplicated") {
                    $("#customerNicknameError").text("중복된 닉네임입니다.");
                    $("#customerNickname").val(""); // 입력 값 지우기
                } else {
					$("#customerNicknameError").text("");
                }
            },
            error: function() {
                $("#customerNicknameError").text("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });
	
	// 비밀번호 실시간 검증 (blur 이벤트 사용)
    $("#customerPassword").blur(function() {
        let customerPassword = $(this).val().trim();
        let passwordMsg = $("#customerPasswordError");
        let passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/;

        if (customerPassword === "") {
            passwordMsg.text("필수 입력 항목입니다."); // 빈 문자열일 때 메시지
            return;
        }

        if (!passwordRegex.test(customerPassword)) {
            passwordMsg.text("비밀번호는 영문자, 숫자, 특수문자를 포함하여 8~15글자여야 합니다.");
        } else {
            passwordMsg.text("");
        }
    });


   // 이름 실시간 검증(keyup, signupForm에서만)
    if ($("#signupForm").length > 0) {
        $("#customerName").keyup(function() {
            let customerName = $(this).val().trim();
            let nameRegex = /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]{1,18}$/;

            if (customerName === "") {
                $("#customerNameError").text("이름을 입력하세요.");
                return;
            }

            if (!nameRegex.test(customerName)) {
                $("#customerNameError").text("이름은 영문(대소문자), 한글, 1~18글자여야 합니다.");
            } else {
                $("#customerNameError").text("");
            }
        });
    }
    
    // 이메일 실시간 검증(keyup)
    $("#customerEmail").keyup(function() {
        let customerEmail = $(this).val().trim();
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (customerEmail === "") {
            $("#customerEmailError").text(""); // 빈 문자열이면 에러 메시지 지움
            return;
        }

        if (!emailRegex.test(customerEmail)) {
            $("#customerEmailError").text("올바른 이메일 형식이 아닙니다.");
        } else {
            $("#customerEmailError").text(""); // 올바른 형식이면 에러 메시지 지움
        }
    });


    // 인증 번호 받기 버튼 클릭 이벤트
    $("#sendVerificationCodeBtn").click(function() {
        let customerEmail = $("#customerEmail").val().trim();
        // 이메일 형식 검사
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (customerEmail === "" || !emailRegex.test(customerEmail)) {
            $("#customerEmailError").text("올바른 이메일 형식이 아닙니다.");
            return;
        }
		
		alert("이메일 발송에 시간이 소요됩니다. 5초만 기다려주세요");
		
        $.ajax({
            url: "/customer/sendVerificationCode",
            type: "POST",
            data: { customerEmail: customerEmail },
            success: function(response) {
                if (response === "success") {
                    alert("인증 번호가 발송되었습니다.");
                    $("#verificationCodeDiv").show();
                    $("#sendVerificationCodeBtn").attr("disabled", true);
                } else {
                    alert("인증 번호 발송에 실패했습니다.");
                }
            },
            error: function() {
                alert("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 인증하기 버튼 클릭 이벤트
    $("#verifyCodeBtn").click(function() {
        let inputCode = $("#verificationCode").val().trim();

        if (inputCode === "") {
             $("#verificationCodeError").text("인증번호를 입력하세요")
            return;
        }

        $.ajax({
            url: "/customer/verifyCode",
            type: "POST",
            data: { inputCode: inputCode },
            success: function(response) {
                if (response === "verified") {
                    alert("인증되었습니다.");
                    $("#verifyCodeBtn").text("인증완료").addClass("verified").attr("disabled", true);
                    $("#verificationCode").attr("readonly", true);
                } else {
                    alert("인증 번호가 일치하지 않습니다.");
                }
            },
            error: function() {
                alert("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 회원 가입 폼 submit 이벤트 (signupForm에서만)
    if ($("#signupForm").length > 0) {
      $("#signupForm").submit(function(event){

	  });
    }

    // 회원 정보 수정 폼 submit 이벤트 (memberInfoForm에서만)
    if ($("#memberInfoForm").length > 0) {
      $("#memberInfoForm").submit(function(event){

      });
    }
});