/* 함수명:chkData(유효성 체크 대상, 메세지 내용)
 출력영역 : alert으로.
 예시 : if(!chkData("#keyword","검색어를")) return; */

 function chkData(item, msg){
 	if($(item).val().replace(/\s/g," ")==""){
 		alert(msg+"입력해 주세요.");
 		$(item).val("");
 		$(item).focus();
 		return false;
 	}else {return true;}
 }
 
 /* 함수명: dataCheck(유효성 체크 대상, 출력 영역, 메시지 내용) */ 
 function dataCheck(item, out, msg){
 	if($(item).val().replace(/\s/g,"")==""){
 		$(out).html(msg + " 입력해 주세요");
 		$(item).val("");
 		return false;
 	}else{
 		return true;
 	}
 }

 /* 함수명: checkForm(유효성 체크 대상, 메시지 내용) 
  * 출력영역: placeholder 속성을 이용.
  * 예시 : if(!checkForm("#keyword","검색어를")) return;
  * */ 
 function checkForm(item, msg) {
 	let message = "";
 	if($(item).val().replace(/\s/g,"")=="") {
 		message = msg + " 입력해 주세요.";
 		$(item).attr("placeholder",message);
 		return false;
 	} else {
 		return true;
 	}
 }
 
 /* 함수명 : getDateFormat(날짜 데이터)
   * 설명 : dateValue의 값을 년-월-일 형식(예시: 2018-01-01)으로 반환 */
  function getDateFormat(dateValue){
	var year = dateValue.getFullYear();
	
	var month = dateValue.getMonth() + 1;
	month  = (month < 10) ? "0"+month : month;
	
	var day = dateValue.getDate();
	day = (day < 10) ? "0"+day : day;
	
	var result = year + "-" + month + "-" + day;
	return result;
  }
 
 const actionProcess = function(form, method, action){
	$(form).attr({
		"method": method,
		"action": action
	});
	$(form).submit();
 }
 
 const locationProcess = function(url){
	location.href = url;
 }