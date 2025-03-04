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

        // AJAX를 사용하여 서버에 결제 정보 전송 (세션에 저장)
        $.ajax({
            url: "/customer/reserve/setPaymentInfo",  // Controller의 핸들러 URL
            type: "POST",
            data: {
                merchantPayKey: merchantPayKey,
                paymentAmount: paymentAmount
            },
            success: function(response) {
                // 서버에서 세션 설정 완료 후, 네이버페이 결제창 열기
                oPay.open({
                    "merchantUserKey":  $("#customerId").val(),
                    "merchantPayKey": merchantPayKey,
                    "productName": "예약금 결제",
                    "totalPayAmount": paymentAmount,
                    "taxScopeAmount": paymentAmount,
                    "taxExScopeAmount": 0,
                    "returnUrl": "http://localhost:8080/customer/reserve/paymentSuccess"
                });
            },
            error: function(xhr, status, error) {
                alert("서버 오류: " + error);
            }
        });
    });

     $("#submitBtn").click(function() {
        // 세션에 paymentSuccess가 있는지 확인 (추가)
        $.ajax({
            type: "GET",
            url: "/customer/reserve/check-payment", // 결제 상태 확인을 위한 엔드포인트
            success: function(paymentStatus) {
                if (paymentStatus === "true") {  // 결제 완료 상태
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
                                window.location.href = "/customer/reserve/complete";
                            } else {
                                $("#errorMessage").text("예약 실패: " + response);
                            }
                        },
                        error: function(xhr, status, error) {
                             $("#errorMessage").text("예약 실패: " + xhr.responseText);
                        }
                    });
                } else {
                    // 결제되지 않았으면 alert
                    alert("예약금을 결제해주세요");
                }
            },
              error: function() {
                alert("결제 상태 확인 중 오류 발생");
            }
        });
    });


    var paymentSuccess = /*[[${paymentSuccess}]]*/ null;
    if (paymentSuccess) {
        alert("결제가 완료되었습니다.");
    }

    var errorMessage = /*[[${errorMessage}]]*/ null;
    if(errorMessage) {
        $("#errorMessage").text(errorMessage);
    }
});