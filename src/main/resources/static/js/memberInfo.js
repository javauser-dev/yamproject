$(document).ready(function() {

    // 입력 값 형식 검사 함수
    function validateInput(inputId, regex, errorMessage, errorSpan) {
        let value = $(inputId).val().trim();

        if (value === "") {
            $(errorSpan).text(""); // 빈 문자열 허용
            return true;
        }

        if (!regex.test(value)) {
            $(errorSpan).text(errorMessage);
            return false;
        }

        $(errorSpan).text("");
        return true;
    }

    // 닉네임 중복 검사 (blur, 현재 사용자 ID 제외)
    $("#customerNickname").blur(function() {
       // ... (이전 코드와 동일) ...
        let customerNickname = $(this).val().trim();
        let currentCustomerId = $("#customerId").val(); // hidden 필드에서 현재 사용자 ID 가져오기

        // 빈 문자열이면 중복 검사 X
        if (customerNickname === "") {
            $("#customerNicknameError").text(""); // 에러 메시지 초기화
            return;
        }

        //형식 검사
        if (!/^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\[\]:;,.<>/?]{2,8}$/.test(customerNickname)) {
            $("#customerNicknameError").text("닉네임은 영문(대/소문자), 숫자, 특수문자, 한글, 2~8자 이내.");
            return;
        }


        $.ajax({
            url: "/customer/checkNickname",
            type: "GET",
            data: { customerNickname: customerNickname, currentCustomerId: currentCustomerId }, // 현재 사용자 ID 추가
            success: function(response) {
                if (response === "duplicated") {
                    $("#customerNicknameError").text("중복된 닉네임입니다.");
                    $("#customerNickname").val("");
                } else {
                    $("#customerNicknameError").text("");
                }
            },
            error: function() {
                $("#customerNicknameError").text("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 비밀번호 실시간 검증(keyup)
    $("#newPassword").keyup(function(){
      // ... (이전 코드와 동일) ...
        let customerPassword = $(this).val().trim();
        let passwordMsg =  $("#newPasswordError");
        let passwordRegex = /^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$/;

        //빈 문자열이면 메시지 지우기.
        if(customerPassword === ""){
            passwordMsg.text("");
            return;
        }

        if(!passwordRegex.test(customerPassword)){
            passwordMsg.text("비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.");
        } else{
            passwordMsg.text("");
        }
    });
	
	// 이메일 형식 검사 (blur 이벤트)
    $("#customerEmail").blur(function() {
        let customerEmail = $(this).val().trim();
        //빈 문자열이면 에러 메시지 지우기.
        if (customerEmail === "") {
          $("#customerEmailError").text("");
          return;
       }
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(customerEmail)) {
            $("#customerEmailError").text("올바른 이메일 형식이 아닙니다.");
        } else {
            $("#customerEmailError").text(""); // 올바른 형식이면 에러 메시지 지움
        }
    });

    // 인증 번호 받기 버튼 클릭 이벤트
    $("#sendVerificationCodeBtn").click(function() {
          let customerEmail = $("#customerEmail").val().trim();

          // 빈 문자열이면 경고 메시지
          if(customerEmail === ""){
            $("#customerEmailError").text("이메일을 입력하세요");
            return;
          }

          // 이메일 형식 검사
          let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          if (!emailRegex.test(customerEmail)) {
              $("#customerEmailError").text("올바른 이메일 형식이 아닙니다.");
              return;
          }
		  
		  alert("인증번호 발송에 시간이 소요됩니다. 5초만 기다려주세요.");

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

	// 인증하기 버튼 클릭 이벤트:  이제 폼 제출은 여기서!
    $("#verifyCodeBtn").click(function() {
		// ... (signup.js와 동일) ...
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
                  $("#verifyCodeBtn").text("인증완료").addClass("verified").attr("disabled", true);
                  $("#verificationCode").attr("readonly", true);
				  // 이메일 변경 폼 자동 제출
				  isValid = true;
                  $("#updateEmailForm").submit();

                } else {
                    alert("인증 번호가 일치하지 않습니다.");
                }
            },
            error: function() {
                alert("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 회원 비밀번호 수정 폼 submit 이벤트
    $("#updatePasswordForm").submit(function(event) {
        // ... (validateInput 함수 사용하여 비밀번호 유효성 검사, 위와 동일) ...
         let isValid = true;

        // 비밀번호 검사. 빈 문자열이면 검사 안함.
         let customerPassword = $("#newPassword").val().trim();
         if(customerPassword !== ""){ //추가
            if (!validateInput("#newPassword", /^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$/, "비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.", "#newPasswordError")) {
                isValid = false;
             }
         }
        if (!isValid) {
            event.preventDefault(); // 폼 제출 막기
        }
    });
	
    //닉네임 변경 submit
     $("#updateNicknameForm").submit(function(event) {
       let isValid = true;

      // 닉네임 검사
      if (!validateInput("#customerNickname", /^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\[\]:;,.<>/?]{2,8}$/, "닉네임은 영문(대/소문자), 숫자, 특수문자, 한글, 2~8자 이내.", "#customerNicknameError")) {
         isValid = false;
       }

      if (!isValid) {
            event.preventDefault(); // 폼 제출 막기
       }
    });
	
    // 이메일 변경 폼 submit 이벤트
    $("#updateEmailForm").submit(function(event) {
        let isValid = true;

        // 이메일 유효성 검사 (빈 문자열이면 수행 안 함)
        let customerEmail = $("#customerEmail").val().trim();
        if (customerEmail !== "" && !validateInput("#customerEmail",  /^[^\s@]+@[^\s@]+\.[^\s@]+$/, "올바른 형식의 이메일을 입력하세요", "#customerEmailError")) {
            isValid = false;
        }

        // 이메일이 변경되었고, 인증이 완료되지 않았으면 폼 제출 막음
        if (!$("#verifyCodeBtn").hasClass("verified")) {
           alert("이메일 인증을 완료해주세요.");
           isValid = false;
       }

        if (!isValid) {
            event.preventDefault();
        }
    });
});