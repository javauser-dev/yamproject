const actionProcess = function(form, method, action){
	$(form).attr({
		"method" : method,
		"action" : action
	});
	$(form).submit();
}

const locationProcess = function(url){
	location.href = url;
}

/* 함수명:chkData(유효성 체크 대상, 메세지 내용)
 출력영역 : alert으로.
 예시 : if(!chkData("#keyword","검색어를")) return; */

function chkData(item, msg){
	if($(item).val().replace(/\s/g," ")==""){
		alert(msg + "입력해 주세요.");
		$(item).val("");
		$(item).focus();
		return false;
	}else { 
		return true;
	}
}

//함수명 dataCheck (유효성 체크대상 출력영역 메시지)
function dataCheck(item, out, msg){
	if($(item).val().replace(/\s/g," ")==""){
		$(out).html(msg + "입력해 주세요.");
		$(item).val("");
		return false;
	}else { 
		return true;
	}
}


//함수명 checkForm (유효성 체크대상, 메시지 내용)
function checkForm(item, msg){
	var message ="";
	if($(item).val().replace(/\s/g," ")==""){
		message + msg + "입력해 주세요.";
		$(item).attr("placeholder",message);
		return false;
	}else { 
		return true;
	}
}

/* 함수명: chkFile(파일명 객체) 
 * 설명: 이미지 파일 여부를 확인하기 위해 확장자 확인 함수. 
 if (!chkFile($("#file"))) return;*/ 
function chkFile(item){
	/*  참고사항
		jQuery.inArray(찾을 값, 검색 대상의 배열): 배열내의 값을 찾아서 인덱스를 반환(요소가 없을 경우-1반환)
		pop(): 배열의 마지막 요소를 제거한 후, 제거한 요소를 반환
	*/
	let ext = item.val().split('.').pop().toLowerCase();
	if(jQuery.inArray(ext, ['gif','png','jpg',]) == -1) {
		alert('gif, png, jpg 파일만 업로드 할 수 있습니다.');
		item.val("");
		return false;
	}else {
		return true;
	}
}

/* 함수명: getDateFormat(날자 데이터) 
 * 설명 : dataValue의 값을 년-월-일 형식(예시: 2018-01-01)으로 반환.*/ 
function getDateFormat(dateValue){
	var year = dateValue.getFullYear(); 
	
	var month = dateValue.getMonth()+1;
	month = (month<10) ? "0"+ month : month;
	
	var day = dateValue.getDate();
	day = (day<10) ? "0"+ day : day;
	
	var result = year+"-"+month+"-"+day;
	return result;
}


