$(document).ready(function() {

    // 입력 값 형식 검사 함수
    function validateInput(inputId, regex, errorMessage, errorSpan) {
        let value = $(inputId).val().trim();

        if (value === "") {
            $(errorSpan).text("필수 입력 항목입니다."); // 필수 입력으로 변경
            return false;
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
    $("#customerPassword").keyup(function(){
        let customerPassword = $(this).val().trim();
        let passwordMsg =  $("#customerPasswordError");
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

    // 이메일 관련 이벤트 핸들러 제거

    // 회원 정보 수정 폼 submit 이벤트
    $("#memberInfoForm").submit(function(event) {
        let isValid = true;

        // 비밀번호 검사 (빈 문자열이면 안됨, 유효한 형식인지 확인)
         if (!validateInput("#customerPassword", /^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$/, "비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.", "#customerPasswordError")) {
              isValid = false;
         }

        // 닉네임 검사 (빈 문자열이면 안됨, 형식 검사)
        if (!validateInput("#customerNickname", /^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!@#$%^&*()_+=-`~{}\[\]:;,.<>/?]{2,8}$/, "닉네임은 영문(대/소문자), 숫자, 특수문자, 한글, 2~8자 이내.", "#customerNicknameError")) {
           isValid = false;
        }

        // 이메일 관련 검증 로직 제거

        if (!isValid) {
            event.preventDefault(); // 폼 제출 막기
        }
    });
});