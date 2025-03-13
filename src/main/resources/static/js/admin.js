document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ 로그인 및 사이드바 JS 로드됨");

    // ✅ 로그인 기능
    const loginButton = document.querySelector(".login-btn");
    const loginForm = document.querySelector("#loginForm");

    if (loginButton && loginForm) {
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
                    sessionStorage.setItem("adminName", result.adminName);
                    window.location.href = result.redirectUrl;
                } else {
                    alert(result.message || "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
                }
            } catch (error) {
                console.error("❌ 로그인 요청 중 오류 발생:", error);
                alert("네트워크 오류 발생! 다시 시도해 주세요.");
            }
        });
    }

    // ✅ 사이드바 메뉴 열기/닫기 기능 추가
    const menuToggles = document.querySelectorAll(".menu-toggle");

    menuToggles.forEach(function (toggle) {
        toggle.addEventListener("click", function () {
            const submenu = this.nextElementSibling;
            if (submenu && submenu.classList.contains("submenu")) {
                submenu.classList.toggle("open");
            }
        });
    });

    console.log("✅ 사이드바 메뉴 토글 기능 추가 완료");

    //---------------------------------------------------------------
    console.log("✅ 이미지 업로드 JS 로딩 완료");

    const profileImage = document.getElementById("profile-image");
    const profileUpload = document.getElementById("profile-upload");

    if (!profileImage || !profileUpload) {
        console.error("❌ profile-image 또는 profile-upload 요소를 찾을 수 없습니다.");
        return;
    }

    // ✅ 사진 클릭 시 파일 업로드 창 열기
    profileImage.addEventListener("click", function () {
        profileUpload.click();
    });

    // ✅ 파일 업로드 시 미리보기 및 서버 업로드
    profileUpload.addEventListener("change", function () {
        const file = profileUpload.files[0];

        if (!file) return;

        // ✅ 파일 크기 체크 (5MB 제한)
        if (file.size > 5 * 1024 * 1024) {
            alert("파일 크기가 5MB를 초과합니다. 작은 용량의 이미지를 업로드해주세요.");
            return;
        }

        // ✅ 이미지 미리보기 (미리보기 기능)
        const reader = new FileReader();
        reader.onload = function (e) {
            profileImage.src = e.target.result;
        };
        reader.readAsDataURL(file);

        // ✅ Ajax로 서버 업로드 요청
        const formData = new FormData();
        formData.append("profileImage", file);

        fetch("/admin/updateProfileImage", {
            method: "POST",
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("프로필 이미지가 변경되었습니다.");
                profileImage.src = data.imageUrl;
            } else {
                alert("이미지 업로드 실패: " + data.message);
            }
        })
        .catch(error => {
            console.error("프로필 이미지 업로드 오류:", error);
            alert("네트워크 오류가 발생했습니다.");
        });
    });
});
