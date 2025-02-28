document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ admin.js가 실행되었습니다.");

    const profileImg = document.querySelector(".profile-img");
    const profileUpload = document.getElementById("profile-upload");
    const menuItems = document.querySelectorAll(".menu-toggle");

    if (!profileImg || !profileUpload || menuItems.length === 0) {
        console.error("❌ 필요한 요소를 찾을 수 없습니다. HTML 구조를 확인하세요.");
        return;
    }

    // 프로필 이미지 클릭 시 파일 업로드 창 열기
    profileImg.addEventListener("click", function () {
        profileUpload.click();
    });

    // 파일 업로드 후 미리보기 설정
    profileUpload.addEventListener("change", function (event) {
        const file = event.target.files[0]; 
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImg.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

    // 메뉴 클릭 시 소메뉴 토글
    menuItems.forEach(item => {
        item.addEventListener("click", function (e) {
            e.preventDefault(); // 기본 링크 동작 방지
            const parent = this.parentElement;
            const submenu = parent.querySelector(".submenu");

            if (submenu) {
                submenu.classList.toggle("open"); // 소메뉴 토글

                // 다른 열린 메뉴 닫기
                document.querySelectorAll(".submenu").forEach(sub => {
                    if (sub !== submenu) {
                        sub.classList.remove("open");
                    }
                });
            }
        });
    });

    console.log("✅ 사이드바 메뉴 토글 기능이 정상적으로 적용되었습니다.");
});
