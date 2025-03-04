$(document).ready(function() {
    // reserveForm.html이 로드될 때, 세션에서 customerId 가져오기
    $.ajax({
        url: '/customer/reserve/getCustomerId', // 세션에서 customerId 가져오는 엔드포인트
        type: 'GET',
        success: function(data) {
            $("#customerId").val(data); // customerId 필드에 설정
        },
        error: function() {
            console.error("Failed to get customerId");
        }
    });

    var oPay = Naver.Pay.create({
        "mode": "development",
        "clientId": "HN3GGCMDdTgGUfl0kFCo",
        "chainId": "b1NiUjJIcGlIZ3B"
    });

	$("#naverPayBtn").click(function() {
        var shopId = $("#shopId").val();
        var paymentAmount = parseInt($("#deposit").val());
        var merchantPayKey = "order-" + new Date().getTime();
        var customerId = $("#customerId").val(); // customerId 가져오기

        // AJAX를 사용하여 서버에 결제 정보 전송 (세션 저장 + DB 저장)
        $.ajax({
            url: "/customer/reserve/setPaymentInfo",  // Controller의 핸들러 URL
            type: "POST",
            data: {
                merchantPayKey: merchantPayKey,
                paymentAmount: paymentAmount,
                customerId: customerId, // customerId 추가
                shopId: shopId          // shopId 추가
            },
            success: function(response) {
                if (response === "success") {
                    // 서버 처리 성공 후, 네이버페이 결제창 열기
                    oPay.open({
                         "merchantUserKey":  $("#customerId").val(),
                        "merchantPayKey": merchantPayKey,
                        "productName": "예약금 결제",
                        "totalPayAmount": paymentAmount,
                        "taxScopeAmount": paymentAmount,
                        "taxExScopeAmount": 0,
                        "returnUrl": "http://localhost:8080/customer/reserve/paymentSuccess"
                    });
                } else {
                    alert("결제 정보 저장 실패"); // 실패 알림
                }
            },
            error: function(xhr, status, error) {
                alert("서버 오류: " + error);
            }
        });
    });

    $("#submitBtn").click(function() {
        // 예약 정보 formData에 저장
        var formData = {
            shopId: parseInt($("#shopId").val()),
            reserveDate: $("#reserveDate").val(),
            reserveTime: $("#reserveTime").val(),
            guestCount: parseInt($("#guestCount").val()),
            deposit: parseInt($("#deposit").val()),
            request: $("#request").val()
        };

        // AJAX를 사용하여 서버에 예약 정보 전송
        $.ajax({
            type: "POST",
            url: "/customer/reserve/new",  // 예약 처리 URL
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(response) {
                if (response === "success") {
                    window.location.href = "/customer/reserve/complete"; // 예약 성공 시 complete 페이지로
                } else {
                    $("#errorMessage").text("예약 실패: " + response);     // 예약 실패 시 에러 메시지 표시
                }
            },
            error: function(xhr, status, error) {
                $("#errorMessage").text("예약 실패: " + xhr.responseText); // 서버 에러 메시지 표시
            }
        });
    });

    // 오류 메시지 표시
    var errorMessage = /*[[${errorMessage}]]*/ null;
    if(errorMessage) {
        $("#errorMessage").text(errorMessage);
    }
});