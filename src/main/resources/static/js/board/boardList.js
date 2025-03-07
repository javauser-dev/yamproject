/* 제목 클릭시 상세 페이지 이동을 위한 처리 이벤트 */		
/*$(".goDetail").on("click", function(){	
	let no =  $(this).parents("tr").data("no");
	locationProcess("/board/"+no);
});*/

/* 페이징 처리 */
$(".page-item a").on("click", function(e){
	e.preventDefault();
	//console.log($(this).data("number"));
	$("#page").val($(this).data("number"));
	actionProcess("#searchForm", "get", "/board/boardList");
})