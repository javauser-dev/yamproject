$(document).ready(function() {

    // 아이디 중복 검사
    $("#customerId").blur(function() { // blur 이벤트: 포커스를 잃었을 때
        let customerId = $(this).val().trim();

        if (customerId !== "") {
            $.ajax({
                url: "/customer/checkId", // 서버 URL
                type: "GET",
                data: { customerId: customerId },
                success: function(response) {
                    if (response === "duplicated") {
                        alert("중복된 아이디입니다.");
                        // setTimeout을 사용하여 alert 창이 닫힌 *후*에 focus() 호출
                        setTimeout(function() {
                            $("#customerId").focus();
                        }, 10); // 아주 짧은 시간(10ms) 지연
						$("#customerId").val("");
                    } else {
                         $("#customerIdError").text(""); // 에러 메시지 제거
                    }
                },
                error: function() {
                    alert("서버와의 통신 중 오류가 발생했습니다.");
                }
            });
        } else {
             $("#customerIdError").text("아이디를 입력하세요."); // 빈칸일경우
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
						$("#customerNickname").val("");
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