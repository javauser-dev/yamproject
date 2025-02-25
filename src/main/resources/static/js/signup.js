$(document).ready(function() {
    $("#signupForm").submit(function(event) {
        let isValid = true;

        // 간단한 클라이언트 측 유효성 검사 (필요에 따라 추가)
        if ($("#customerId").val().trim() === "") {
            $("#customerIdError").text("아이디를 입력하세요.");
            isValid = false;
        } else {
            $("#customerIdError").text("");
        }
		// ... (다른 필드 검사) ...
        if ($("#customerPassword").val().trim() === "") {
           $("#customerPasswordError").text("비밀번호를 입력하세요");
           isValid = false;
        } else {
           $("#customerPasswordError").text("");
        }

        if (!$("#agree").is(":checked")) {
            $("#agreeError").text("회원가입에 동의해야 합니다.");
            isValid = false;
        } else {
            $("#agreeError").text("");
        }


        if (!isValid) {
            event.preventDefault(); // 폼 제출 막기
        }
    });
});