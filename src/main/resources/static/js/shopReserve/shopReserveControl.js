$("#articleInsertBtn").on("click", function(){
	if(!chkData("#name", "작성자명을")) return;
	else if(!chkData("#title", "제목을")) return;
	else if(!chkData("#content", "내용을")) return;
	else{
		actionProcess("#insertForm", "post", "/shopReserve/articleInsert");
	}
});

$("#articleUpdateBtn").on("click", function(){
	if(!chkData("#guestCount", "인원수를")) return;
	else if(!chkData("#reserveRequest", "요청사항을")) return;
	else{
		actionProcess("#updateForm", "post", "/shopReserve/articleUpdate");
	}
});

$("#articleCancelBtn").on("click", function(){
	let form = $(this).parents("form").attr("id");
	//console.log(form)
	$("#"+form+"").each(function(){
		this.reset();
	});
});

$("#shopReserveListBtn").on("click", function(){
	locationProcess("/shopReserve/shopReserveList");
});

$("#insertFormBtn").click(function(){
	locationProcess("/shopReserve/insertForm");
});

/* 수정 버튼 클릭 시 처리 이벤트 */
$("#updateFormBtn").click(function(){
	actionProcess("#dataForm", "post", "/shopReserve/updateForm");
});

/* 삭제 버튼 클릭 시 처리 이벤트 */
$("#shopReserveDeleteBtn").click(function(){
	if(confirm("정말로 삭제하시겠습니까?")){
		actionProcess("#dataForm", "post", "/shopReserve/articleDelete");	
	}
});

$(".goDetail").on("click", function(){	
	let shopReserveNo =  $(this).parents("div").data("shopReserveNo");
	locationProcess("/shopReserve/"+shopReserveNo);
});

/*$(".goDetail").on("click", function(){	
	let no =  $(this).parents("tr").data("no");
	locationProcess("/shopReserve/"+no);
});*/