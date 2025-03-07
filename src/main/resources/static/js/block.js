document.getElementById('loginForm').addEventListener('submit', function(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  var formData = new FormData(this);

  fetch('/login', {
    method: 'POST',
    body: formData
  })
  .then(response => {
    if (response.ok) {
      window.location.href = '/customer/myPage'; // 로그인 성공 시 마이페이지로 이동
    } else {
      alert('로그인 실패');
    }
  })
  .catch(error => {
    console.error('Error:', error);
  });
});

document.getElementById('logoutForm').addEventListener('submit', function(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  fetch('/logout', {
    method: 'POST'
  })
  .then(response => {
    if (response.ok) {
      window.location.href = '/main'; // 로그아웃 성공 시 메인페이지로 이동
    } else {
      alert('로그아웃 실패');
    }
  })
  .catch(error => {
    console.error('Error:', error);
  });
});