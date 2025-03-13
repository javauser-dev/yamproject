let buttonCheck="";

const pwdInit=function(value){
	$("#pwdFormateArea").css("visibility", value);
}

const btnInit = function(){
	$("#message").removeClass("board-msg-error-color");
	$("#message").addClass("board-msg-default-color");
	$("#message").text("작성시 입력한 비밀번호를 입력해주세요.");
}

pwdInit("hidden");
/*수정 버튼 클릭시 처리 이벤트 */

$("#updateFormBtn").on("click", function(){
	//actionProcess("#dataForm", "get", "/board/updateForm");
	
	pwdInit("visible");
	btnInit();
	buttonCheck = "updateButton";
});

/* 삭제버튼 클릭 시 처리 이벤트 */
$("#boardDeleteBtn").on("click", function(){
	/*if(confirm("정말 삭제하시겠습니까?")){
	actionProcess("#dataForm", "get", "/board/deleteForm");
	*/
	
	pwdInit("visible");
	btnInit();
	buttonCheck = "deleteButton";
});

$("#boardPasswd").on("keyup", ()=>{
	btnInit();
	
});

/*댓글이 존재하면 '게시글을 지울 수 없습니다' 라고 알림창 띄우고 다시 상세 게시글로 되돌아가는 제어? */

/*비번 입력창에서 키 처리 이벤트 */
$("#passwdBtn").on("click", function(){
	if(!dataCheck("#boardPasswd", "#message", "비번을"))return;
	else{
		$.ajax({
			url:"/board/pwdConfirm",
			data:$("#pwdForm").serialize(),
			method: "post",
			dataType : "text"
		}).done(function(resultData){
			if(resultData=="불일치"){
				$("#message").text("작성시 입력한 비번이 일치하지 않습니다.");
				$("#message").removeClass("board-msg-default-color");
				$("#message").addClass("board-msg-error-color");
				$("#boardPasswd").select();
			}else if(resultData=="일치"){
				$("#message").text("");
				if(buttonCheck=="updateButton"){
					actionProcess("#dataForm", "get", "/board/updateForm");
				}else if(buttonCheck == "deleteButton"){
					if(confirm("정말 삭제하시겠습니까?")){
						actionProcess("#dataForm", "post", "/board/boardDelete");
					}
				}
			}
		}).fail(function(){
			alert('시스템 오류 입니다. 관리자에게 문의 하세요.');
		});
	}
});

$("#passwdCancelBtn").on("click", function(){
	$("#boardPasswd").val("");
	pwdInit("hidden");
	buttomCheck="";
});
$("#insertFormBtn").on("click", function(){
	locationProcess("/board/insertForm");
});

$("#boardListBtn").on("click", function(){
	locationProcess("/board/boardList");
});

/* 수정 버튼 클릭 시 처리 이벤트 */
$("#updateFormBtn").click(function(){	
	actionProcess("#dataForm", "post", "/board/updateForm");
});
	
/* 삭제 버튼 클릭 시 처리 이벤트 */
$("#boardDeleteBtn").click(function(){	
	if(confirm("정말 삭제하시겠습니까?")){
		actionProcess("#dataForm", "post", "/board/boardDelete");
	}
});