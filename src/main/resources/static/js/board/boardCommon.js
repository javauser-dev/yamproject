/* 글쓰기 버튼 제어 */
$("#insertFormBtn").click(function(){
	locationProcess("/board/insertForm");
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
	locationProcess("/board/boardList");
});