document.addEventListener("DOMContentLoaded", function () {
    // 🔹 회원 통계 데이터 가져오기
    fetch("/stats")
        .then(response => response.json())
        .then(data => {
            console.log("📊 전체 회원 통계 데이터:", data);

            let newUsers = data.newUsers || 0;
            let deletedUsers = data.deletedUsers || 0;  

            let ctx1 = document.getElementById('chart1').getContext('2d');
            new Chart(ctx1, {
                type: 'doughnut',
                data: {
                    labels: [`신규회원 (${newUsers}명)`, `탈퇴회원 (${deletedUsers}명)`], // 🔹 숫자 포함
                    datasets: [{
                        data: [newUsers, deletedUsers], // 데이터 반영
                        backgroundColor: ['blue', 'red']
                    }]
                }
            });
        }).catch(error => console.error("❌ 회원 통계 데이터 불러오기 실패:", error));

    // 🔹 기존 차트 (매장 관련)
    let ctx2 = document.getElementById('chart2').getContext('2d');
    new Chart(ctx2, {
        type: 'doughnut',
        data: {
            labels: ['신규 매장', '폐점 매장'],
            datasets: [{
                data: [330, 110], 
                backgroundColor: ['green', 'orange']
            }]
        }
    });

	// 🔹 기존 사업자 통계 가져오기
	    fetch("/storeStats")
	        .then(response => response.json())
	        .then(data => { 
	            console.log("📊 전체 사업자 통계 데이터:", data);

	            let newStores = data.newStores || 0;
	            let deleteStores = data.deleteStores || 0;
 
	            let ctx3 = document.getElementById('chart3').getContext('2d');
	            new Chart(ctx3, {
	                type: 'doughnut',
	                data: { 
	                    labels: [`전체 사업자 (${newStores}명)`, `탈퇴 사업자 (${deleteStores}명)`],
	                    datasets: [{
	                        data: [newStores, deleteStores],
	                        backgroundColor: ['purple', 'gray']
	                    }]
	                }
	            });
	        }).catch(error => console.error("❌ 사업자 통계 데이터 불러오기 실패:", error));
	});
