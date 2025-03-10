/** 새로운 글을 화면에 추가하기(보여주기) 위한 함수*/
const template = (replyNumber, replyName, replyContent, replyDate) => {
	let $div = $('#replyList');
	 
	let $element = $('#item-template').clone().removeAttr('id');
	$element.attr("data-num", replyNumber);
	$element.addClass("reply");
    $element.find('.card-header .name').html(replyName);
    $element.find('.card-header .date').html(" / " + replyDate); 
    $element.find('.card-body .card-text').html(replyContent);

    $div.append($element); 
};

const listAll = () => {
	$(".reply").detach(); // detach(): 선택한 요소를 DOM 트리에서 삭제.
	let boardNumber = $("#boardNumber").val();
	let url = "/replies/all/"+boardNumber; 
	$.getJSON(url, function(data) { //data = [{replyNumber:1, replyName:"홍길동"}, ...{}].
		$(data).each(function() {
			let replyNumber = this.replyNumber; //하나씩 꺼내 받아 왔음. each로 반복하여. 하났기 매개변수로.
			let replyName = this.replyName;
			let replyContent = this.replyContent;
			let replyDate = this.replyDate; 
			replyContent = replyContent.replace(/(\r\n|\r|\n)/g, "<br />");

			template(replyNumber, replyName, replyContent, replyDate);
		});

	}).fail(function() {
		alert("덧글 목록을 불러오는데 실패하였습니다. 잠시후에 다시 시도해 주세요.");
	});	
};


/* 요청 */
listAll();

/* 입력 폼 초기화 */
const dataReset = () => {
	$("#replyForm").each(function(){
		this.reset();
	});
};


/*글입력을 위한 Ajax연동처리 */
$("#replyInsertBtn").on("click", function(){
	let boardNumber = $("#boardNumber").val();
	const insertUrl="/replies/replyInsert";
	
	if(!checkForm("#replyName","작성자를")) return;
	else if(!checkForm("#replyPasswd", "비밀번호를")) return;
	else if (!checkForm("#replyContent", "댓글내용을"))return;
	else{
		/*JSON.stringify(): javascript 값이나 객체를 JSON문자열로 변환. */
		const value = {
			replyName:$("#replyName").val(),
			replyPasswd:$("#replyPasswd").val(),
			replyContent:$("#replyContent").val(),
			board:{
				boardNumber:boardNumber,
			}
		};
		$.ajax({
			url:insertUrl,
			method: "post",
			headers:{
				"Content-Type":"application/json"
			},
			data: JSON.stringify(value),
			dataType:"text"
		}).done(function(resultData){
			if(resultData=="SUCCESS"){
				alert("댓글 등록이 완료.");
				dataReset();
				listAll(boardNumber);
				}
			}).fail(function(){
				alert('시스템오류입니다. 관리자에게 문의하세요');
			});
	}
	
});