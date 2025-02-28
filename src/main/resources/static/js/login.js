document.addEventListener("DOMContentLoaded", function () {
    console.log("로그인 JS 로드됨");
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

        // AJAX 방식으로 로그인 처리
        const formData = new FormData(loginForm);
        
        try { 
            const response = await fetch("/api/login", {
                method: "POST",
                body: new URLSearchParams(formData),
                headers: { 
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Accept": "application/json"
                }
            });

            const result = await response.json();
            
            if (response.ok && result.success) {
                // 🔥 ID가 'admin'으로 시작하면 /dashboard, 아니면 /main으로 이동
                const redirectUrl = idInput.value.startsWith("admin") ? "/dashboard" : "/main";
                window.location.href = redirectUrl;
            } else {
                // 로그인 실패
                alert(result.message || "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        } catch (error) {
            console.error("로그인 요청 중 오류 발생:", error);
            
            // 🔥 네트워크 오류 시 사용자에게 안내
            alert("네트워크 오류 발생! 다시 시도해 주세요.");

            // 🔥 AJAX 실패 시 일반 폼 제출로 fallback
            setTimeout(() => loginForm.submit(), 1000);  // 1초 후 폼 제출
        }
    });
});
