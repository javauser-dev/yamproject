function confirmCancel(reserveId, reserveCheck) {
    if (reserveCheck === 0) {
        if (confirm("예약을 취소하시겠습니까?")) {
            // URL 생성 (Thymeleaf 사용 부분은 HTML에서 처리)
             var url = "/customer/reserve/cancel?id=" + reserveId; // 직접 문자열
            window.location.href = url;
        }
    } else {
        alert("매장이 예약을 승인한 상태입니다. 매장에 직접 연락하세요.");
    }
}


function initMap(address, shopName) {  // 주소와 가게 이름을 파라미터로 받음

    // 카카오맵 API 초기화 및 지도 생성
    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표 (임시 좌표)
            level: 3 // 지도의 확대 레벨
        };

    // 지도 생성
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // 주소-좌표 변환 객체 생성
    var geocoder = new kakao.maps.services.Geocoder();

    // 주소로 좌표를 검색
    geocoder.addressSearch(address, function(result, status) {

        // 정상적으로 검색이 완료됐으면
         if (status === kakao.maps.services.Status.OK) {

            var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

            // 결과값으로 받은 위치를 마커로 표시
            var marker = new kakao.maps.Marker({
                map: map,
                position: coords
            });

            // 인포윈도우로 장소에 대한 설명을 표시
            var infowindow = new kakao.maps.InfoWindow({
                content: '<div style="width:150px;text-align:center;padding:6px 0;">' + shopName + '</div>'  // 가게 이름 (파라미터 사용)
            });
            infowindow.open(map, marker);

            // 지도의 중심을 결과값으로 받은 위치로 이동
            map.setCenter(coords);
        } else {
             // 주소 검색 실패 시 처리
            console.error("주소 검색 실패:", status);
            // 지도를 숨기거나 오류 메시지 표시
            document.getElementById('map').style.display = 'none';
            // 또는 alert("주소를 찾을 수 없습니다.");

            //+ 주소가 없을 때 기본 맵 띄우기.
            var coords = new kakao.maps.LatLng(33.450701, 126.570667); // 기본 위치 (예: 제주도)

            // 결과값으로 받은 위치를 마커로 표시합니다
            var marker = new kakao.maps.Marker({
              map: map,
              position: coords,
            });

            // 인포윈도우로 장소에 대한 설명을 표시합니다
            var infowindow = new kakao.maps.InfoWindow({
              content:
                '<div style="width:150px;text-align:center;padding:6px 0;">가게 위치를 찾을 수 없습니다.</div>',
            });
            infowindow.open(map, marker);

            // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
            map.setCenter(coords);
        }
    });
}