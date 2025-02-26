$(document).ready(function() {

  // 새 비밀번호 형식 검사 (실시간, keyup 이벤트)
  $("#newPassword").keyup(function() {
        let newPassword = $(this).val().trim();
        let passwordRegex = /^(?:[a-zA-Z]{8,15}|[0-9]{8,15}|[@$!%*?&]{8,15})$/;

        if (newPassword === "") {
            $("#newPasswordError").text(""); // 빈 문자열이면 메시지 지움
            return;
        }

        if (!passwordRegex.test(newPassword)) {
            $("#newPasswordError").text("비밀번호는 영문자, 숫자, 특수문자 중 하나 이상을 사용하여 8~15글자여야 합니다.");
        } else {
            $("#newPasswordError").text("");
        }
    });
});