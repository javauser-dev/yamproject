/*$("#boardUpdateBtn").on("click", function(){
	if(!chkData("#title", "제목을")) return;
	else if(!chkData("#content", "내용을")) return;
	else{
		actionProcess("#updateForm", "post", "/board/boardUpdate");
	}
});*/

$("#boardUpdateBtn").on("click", function(){
	if(!chkData("#title", "제목을")) return;
	else if(!chkData("#content", "내용을")) return;
	else{
		if($("#file").val() != ""){  // 업로드할 이미지 파일이 존재한다면
			if(!chkFile("#file")) return; // 이미지 파일만 업로드 가능
		}
		actionFileProcess("#updateForm", "post", "/board/boardUpdate");
	}
});