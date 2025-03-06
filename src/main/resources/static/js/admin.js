document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ 로그인 JS 로드됨");

    const loginButton = document.querySelector(".login-btn");
    const loginForm = document.querySelector("#loginForm");

    loginButton.addEventListener("click", async function (event) {
        event.preventDefault(); // 기본 제출 방지

        const idInput = loginForm.querySelector('input[name="id"]');
        const passwordInput = loginForm.querySelector('input[name="password"]');

        if (!idInput.value || !passwordInput.value) {
            alert("아이디와 비밀번호를 모두 입력해주세요.");
            return;
        }

        const formData = new URLSearchParams(new FormData(loginForm));

        try {
            const response = await fetch("/api/login", {
                method: "POST",
                body: formData,
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Accept": "application/json"
                }
            });

            const result = await response.json();

            if (response.ok && result.success) {
                // ✅ 서버에서 전달된 redirectUrl 사용
                window.location.href = result.redirectUrl;
            } else {
                alert(result.message || "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        } catch (error) {
            console.error("❌ 로그인 요청 중 오류 발생:", error);
            alert("네트워크 오류 발생! 다시 시도해 주세요.");
        }
    });
});
