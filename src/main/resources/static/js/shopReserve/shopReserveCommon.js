/* 글쓰기 버튼 제어 */
$("#insertFormBtn").click(function(){
	locationProcess("/shopReserve/insertForm");
});

/* 취소 버튼 제어 */
$("#boardCancelBtn").on("click", function(){
	let form = $(this).parents("form").attr("id");
	console.log(form);
	$("#"+form+"").each(function(){
		this.reset();
	});
});

/* 목록 버튼 제어 */
$("#boardListBtn").click(function(){
	locationProcess("/shopReserve/shopReserveList");
});


/*$("#boardCancelBtn").on("click", function(){
	$("#insertForm").each(function(){
		this.reset();
	});
});

$("#boardListBtn").on("click", function(){
	locationProcess("/board/boardList");
});*/