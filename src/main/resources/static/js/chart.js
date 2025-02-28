document.addEventListener("DOMContentLoaded", function () {
           // 첫 번째 차트: 도넛 그래프 (회원 가입/탈퇴 비율)
           let ctx1 = document.getElementById('chart1').getContext('2d');
           new Chart(ctx1, {
               type: 'doughnut',
               data: {
                   labels: ['신규회원', '탈퇴회원'],
                   datasets: [{
                       data: [60, 40], // 예제 데이터
                       backgroundColor: ['blue', 'red']
                   }]
               }
           });

           // 두 번째 차트: 막대 그래프 (신규 매장/폐점 매장 수)
           let ctx2 = document.getElementById('chart2').getContext('2d');
           new Chart(ctx2, {
               type: 'bar',
               data: {
                   labels: ['신규 매장', '폐점 매장'],
                   datasets: [{
                       label: '매장 수',
                       data: [330, 110], // 예제 데이터 (신규 매장: 30개, 폐점 매장: 10개)
                       backgroundColor: ['green', 'orange']
                   }]
               },
               options: {
                   responsive: true,
                   scales: {
                       y: {
                           beginAtZero: true
                       }
                   }
               }
           });
       });