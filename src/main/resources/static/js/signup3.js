$(document).ready(function() {

    // 아이디 중복 검사
    $("#customerId").blur(function() {
        let customerId = $(this).val().trim();

        if (customerId !== "") {
            $.ajax({
                url: "/customer/checkId",
                type: "GET",
                data: { customerId: customerId },
                success: function(response) {
                    if (response === "duplicated") {
                        alert("중복된 아이디입니다.");
                        setTimeout(function() {
                            $("#customerId").focus();
                        }, 10);
                    } else {
                        $("#customerIdError").text("");
                    }
                },
                error: function() {
                    alert("서버와의 통신 중 오류가 발생했습니다.");
                }
            });
        } else {
            $("#customerIdError").text("아이디를 입력하세요.");
        }
    });

    // 닉네임 중복 검사
    $("#customerNickname").blur(function() {
        let customerNickname = $(this).val().trim();

        if (customerNickname !== "") {
            $.ajax({
                url: "/customer/checkNickname",
                type: "GET",
                data: { customerNickname: customerNickname },
                success: function(response) {
                    if (response === "duplicated") {
                        alert("중복된 닉네임입니다.");
                        setTimeout(function() {
                            $("#customerNickname").focus();
                        }, 10);
                    } else {
                         $("#customerNicknameError").text("");
                    }
                },
                error: function() {
                    alert("서버와의 통신 중 오류가 발생했습니다.");
                }
            });
        }else{
             $("#customerNicknameError").text("닉네임을 입력하세요");
        }
    });


    // 인증 번호 받기 버튼 클릭 이벤트
    $("#sendVerificationCodeBtn").click(function() {
        let customerEmail = $("#customerEmail").val().trim();

        if (customerEmail === "") {
            alert("이메일을 입력하세요.");
            return;
        }

        // 이메일 형식 검사 (간단한 예시, 필요에 따라 더 엄격하게)
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(customerEmail)) {
            alert("올바른 이메일 형식이 아닙니다.");
            return;
        }
		
		alert("5초만 잠시만 기다려주세요");


        $.ajax({
            url: "/customer/sendVerificationCode",
            type: "POST", // POST 요청으로 변경
            data: { customerEmail: customerEmail },
            success: function(response) {
                if (response === "success") {
                    alert("인증 번호가 발송되었습니다.");
                    $("#verificationCodeDiv").show(); // 인증 번호 입력 필드 표시
                    $("#sendVerificationCodeBtn").attr("disabled", true); //버튼 비활성화
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
            alert("인증 번호를 입력하세요.");
            return;
        }

        $.ajax({
            url: "/customer/verifyCode",
            type: "POST", // POST 요청
            data: { inputCode: inputCode },
            success: function(response) {
                if (response === "verified") {
                    alert("인증되었습니다.");
                    $("#verifyCodeBtn").text("인증완료").addClass("verified").attr("disabled", true);
                    $("#verificationCode").attr("readonly", true); // 인증번호 입력 필드 읽기 전용으로
                } else {
                    alert("인증 번호가 일치하지 않습니다.");
                }
            },
            error: function() {
                alert("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });


    $("#signupForm").submit(function(event) {
        let isValid = true;

        // 아이디 검사
        if ($("#customerId").val().trim() === "") {
            $("#customerIdError").text("아이디를 입력하세요.");
            isValid = false;
        } else {
            $("#customerIdError").text("");
        }

        // 비밀번호 검사
        if ($("#customerPassword").val().trim() === "") {
           $("#customerPasswordError").text("비밀번호를 입력하세요");
           isValid = false;
        } else {
           $("#customerPasswordError").text("");
        }

        //이메일 검사: 추가된 부분
        if($("#customerEmail").val().trim() === ""){
            $("#customerEmailError").text("이메일을 입력하세요");
            isValid = false;
        } else{
            $("#customerEmailError").text("");
        }

        // 동의 체크 검사
        if (!$("#agree").is(":checked")) {
            $("#agreeError").text("회원가입에 동의해야 합니다.");
            isValid = false;
        } else {
            $("#agreeError").text("");
        }

        // 날짜 형식 검사 (yyyy-MM-dd)
        let dateStr = $("#customerBirthDate").val();
        let dateRegex = /^\d{4}-\d{2}-\d{2}$/; // yyyy-MM-dd 형식 정규식

        if (!dateRegex.test(dateStr)) {
            alert("생년월일을 yyyy-MM-dd 형식으로 입력하세요."); // 사용자에게 알림
            isValid = false;
        } else {
             let date = new Date(dateStr); // Date 객체로 변환하여 유효성 검사
             if (isNaN(date.getTime())) { // 유효하지 않은 Date 객체
                 alert("유효하지 않은 날짜입니다.");
                 isValid = false;
             }
        }


        if (!isValid) {
            event.preventDefault(); // 폼 제출 막기
        }
    });
});