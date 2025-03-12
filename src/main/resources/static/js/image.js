document.addEventListener("DOMContentLoaded", function() {
    const profileImage = document.getElementById("profile-image");
    const profileUpload = document.getElementById("profile-upload");

    if (!profileImage || !profileUpload) {
        console.error("❌ profile-image 또는 profile-upload 요소를 찾을 수 없습니다.");
        return;
    }

    // ✅ 사진 클릭 시 파일 업로드 창 열기
    profileImage.addEventListener("click", function() {
        profileUpload.click();
    });

    // ✅ 파일 업로드 시 미리보기 및 서버 업로드
    profileUpload.addEventListener("change", function() {
        const file = profileUpload.files[0];
 
        if (!file) return;

        // ✅ 파일 크기 체크 (5MB 제한)
        if (file.size > 5 * 1024 * 1024) {
            alert("파일 크기가 5MB를 초과합니다. 작은 용량의 이미지를 업로드해주세요.");
            return;
        }

        // ✅ 이미지 미리보기
        const reader = new FileReader();
        reader.onload = function(e) {
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
