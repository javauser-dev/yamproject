/**
 * 
 */$(document).ready(function() {
        const menus = [
            { name: "🍕 피자", image: "images/pizza.jpg" },
            { name: "🍣 초밥", image: "images/sushi.jpg" },
            { name: "🍔 햄버거", image: "images/burger.jpg" },
            { name: "🍜 라멘", image: "images/ramen.jpg" },
            { name: "🥗 포케", image: "images/salad.jpg" },
            { name: "🍗 치킨", image: "images/chicken.jpg" }
        ];

        $("#randomMenuBtn").on("click", function() {
            let spinCount = 0;
            const maxSpins = 20;  

            $("#menuModal").modal("show"); // ✅ 모달창 열기
            
            const interval = setInterval(function() {
                const randomIndex = Math.floor(Math.random() * menus.length);
                $("#slotMenuTitle").text(menus[randomIndex].name); // ✅ 모달 내 제목 변경
                $("#slotMenuImage").attr("src", menus[randomIndex].image); // ✅ 모달 내 이미지 변경
                
                spinCount++;
                if (spinCount >= maxSpins) {
                    clearInterval(interval);  
                    
                    // ✅ 최종 추천 메뉴 선택
                    setTimeout(() => {
                        const finalIndex = Math.floor(Math.random() * menus.length);
                        $("#slotMenuTitle").text(`🎉 오늘의 추천 메뉴: ${menus[finalIndex].name}`);
                        $("#slotMenuImage").attr("src", menus[finalIndex].image);

                        startFireworks(); // ✅ 폭죽 애니메이션 시작
                    }, 500);
                }
            }, 100);
        });

        // ✅ 폭죽 효과 (Canvas)
        function startFireworks() {
            const canvas = document.getElementById("fireworksCanvas");
            canvas.width = 300;
            canvas.height = 200;
            const ctx = canvas.getContext("2d");

            let particles = [];

            function createParticles() { 
                for (let i = 0; i < 30; i++) {
                    particles.push({
                        x: canvas.width / 2,
                        y: canvas.height / 2,
                        angle: Math.random() * Math.PI * 2,
                        speed: Math.random() * 4 + 1,
                        size: Math.random() * 3 + 1,
                        opacity: 1
                    });
                }
            }

            function updateParticles() {
                particles.forEach((p, i) => {
                    p.x += Math.cos(p.angle) * p.speed;
                    p.y += Math.sin(p.angle) * p.speed;
                    p.opacity -= 0.02;
                    if (p.opacity <= 0) particles.splice(i, 1);
                });
            }

            function drawParticles() {
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                particles.forEach(p => {
                    ctx.beginPath();
                    ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
                    ctx.fillStyle = `rgba(255, 200, 0, ${p.opacity})`;
                    ctx.fill();
                });
            }

            function animate() {
                updateParticles();
                drawParticles();
                if (particles.length > 0) requestAnimationFrame(animate);
            }

            createParticles();
            animate();
        }
    });