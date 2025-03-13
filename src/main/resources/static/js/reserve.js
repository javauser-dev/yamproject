$(document).ready(function() {
			let selectedDate = "-";
			let selectedTime = "-";
			let selectedPeople = "-";

			// 날짜 선택 이벤트
			$("#myDate").on("change", function() {
				selectedDate = $(this).val();
			});

			// 시간 선택 이벤트
			$(".time-btn").on("click", function() {
				$(".time-btn").removeClass("btn-success").addClass("btn-light");
				$(this).removeClass("btn-light").addClass("btn-success");
				selectedTime = $(this).data("time");
			});

			// 인원 선택 이벤트
			$(".people-btn").on("click", function() {
				$(".people-btn").removeClass("btn-success").addClass("btn-light");
				$(this).removeClass("btn-light").addClass("btn-success");
				selectedPeople = $(this).data("people");
			});

			// 예약 확인 버튼 클릭 시, 선택한 정보 반영
			$("#confirmReservationBtn").on("click", function() {
				$("#selectedDate").text(selectedDate);
				$("#selectedTime").text(selectedTime);
				$("#selectedPeople").text(selectedPeople);
			});
		});