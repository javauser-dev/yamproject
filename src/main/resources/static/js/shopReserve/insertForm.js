/*$("#boardInsertBtn").on("click", function(){
	if(!chkData("#name", "작성자명을")) return;
	else if(!chkData("#title", "제목을")) return;
	else if(!chkData("#content", "내용을")) return;
	else if(!chkData("#passwd", "비밀번호를")) return;
	else{
		actionFileProcess("#insertForm", "post", "/board/boardInsert");
	}
});*/

$("#boardInsertBtn").on("click", function(){
	if(!chkData("#name", "작성자명을")) return;
	else if(!chkData("#title", "제목을")) return;
	else if(!chkData("#content", "내용을")) return;
	else if(!chkData("#passwd", "비밀번호를")) return;
	
	//else if (!chkData("#file", "업로드할 이미지 파일을")) return; 필수요소
	else{
		if($("#file").val()!=""){ //업로드할 이미지 파일이 존재한다면
			if(!chkFile("#file")) return; //이미지 파일만 업로드 가능
		}
		actionFileProcess("#insertForm", "post", "/shopReserve/boardInsert");
	}
});

