$(document).ready(function() {

    // +추가 customerId 도 넣어줌
    var customerId = /*[[${customerId}]]*/ null;
    if(customerId) {
        $("#customerId").val(customerId);
    }

    // 네이버페이 결제 연동
   var oPay = Naver.Pay.create({
        "mode" : "development",  // 또는 "production"
        "clientId": "HN3GGCMDdTgGUfl0kFCo", // 실제 클라이언트 아이디로 변경
        "chainId": "b1NiUjJIcGlIZ3B"
    });

    $("#naverPayBtn").click(function() {
        var shopId = $("#shopId").val(); // shopId 가져오기
        var paymentAmount = parseInt($("#deposit").val());
        var merchantPayKey = "order-" + new Date().getTime(); // 고유한 merchantPayKey 생성

        oPay.open({
            "merchantUserKey": $("#customerId").val(),
            "merchantPayKey": merchantPayKey,
            "productName": "예약금 결제",
            "totalPayAmount": paymentAmount,
            "taxScopeAmount": paymentAmount,
            "taxExScopeAmount": 0,
            // returnUrl에 shopId 추가
            "returnUrl": "http://localhost:8080/customer/reserve/payment-success?merchantPayKey=" + merchantPayKey + "&paymentAmount=" + paymentAmount + "&shopId=" + shopId
        });
    });


    $("#submitBtn").click(function() {
        var formData = {
            shopId: parseInt($("#shopId").val()),
            reserveDate: $("#reserveDate").val(),
            reserveTime: $("#reserveTime").val(),
            guestCount: parseInt($("#guestCount").val()),
            deposit: parseInt($("#deposit").val()),
            request: $("#request").val()
        };

        $.ajax({
            type: "POST",
            url: "/customer/reserve/new",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(response) {
                if (response === "success") {
                    window.location.href = "/customer/reserve/complete"; // 예약 성공 시 리다이렉트
                } else {
                    $("#errorMessage").text("예약 실패: " + response); // 예약 실패 메시지
                }
            },
            error: function(xhr, status, error) {
                $("#errorMessage").text("예약 실패: " + xhr.responseText); // 서버 에러 메시지
            }
        });
    });

	// +추가 : payment-success에서 돌아왔을 때 alert 띄우기
    if (window.location.search.includes("paymentSuccess=true")) {
      alert("결제가 완료되었습니다.");
      // URL 파라미터에서 paymentSuccess 제거 (선택 사항)
      history.replaceState({}, document.title, window.location.pathname);
    }

     // +추가 :  에러메시지 출력
    var errorMessage = /*[[${errorMessage}]]*/ null;
    if(errorMessage) {
        $("#errorMessage").text(errorMessage);
    }

});