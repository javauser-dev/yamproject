const $keyword = $("#keyword");
const $search = $("#search");
let value = "";
let regex;

const highlightKeyword = function(element, keyword, regex) {
    const html = element.html();
    element.html(html.replace(regex, `<span class='board-required'>${keyword}</span>`));
}

const searchKeyword = function() {
    const keyword = $keyword.val();
    const search = $search.val();

    if ((keyword !== "" || startDate !== "") && search !== "b_content") {
        value = search === 'b_title' ? ".list tr .goDetail"
             : search === 'b_name' ? ".list tr td.name"
             : search === 'b_date' ? ".list tr td.date"
             : "";

        $(value).filter(`:contains(${keyword})`).each(function () {
            const $this = $(this);

            if (search !== 'b_date') {
                regex = new RegExp(keyword, 'gi');
                highlightKeyword($this, keyword, regex);
            } else {
                regex = /^([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/g;
                highlightKeyword($this, $this.html(), regex);
            }
        });
    }
}

const dateSearchDisplay = function() {
    if ($search.val() !== "b_date") {
        $(".dateArea").hide();
        $(".textArea").show();
        $keyword.focus();
    } else {
        $(".textArea").hide();
        $(".dateArea").show();
    }
}

const resetDateFields = function() {
    let now = new Date();
    $("#startDate").val(getDateFormat(new Date(now.setMonth(now.getMonth() - 1))));
    $("#endDate").val(getDateFormat(new Date()));
}

/* 입력 양식 enter 제거 */
$keyword.on("keydown", function (event) {
	if (event.keyCode === 13) {
		event.preventDefault();
	}
});
	

$("#searchBtn").on("click", function () {
	if ($search.val() !== "b_date") {
		$("#startDate").val("");
		$("#endDate").val("");
		if (!chkData("#keyword", "검색어를")) return;
	} else {
		$("#keyword").val("");
		if (!chkData("#startDate", "시작날자를")) return;
		if (!chkData("#endDate", "종료날자를")) return;
		if ($('#startDate').val() > $('#endDate').val()) {
			alert("시작날자가 종료날자보다 더 클 수 없습니다.");
			return;
		}
	}
	$("#pageNum").val(1);
	actionProcess("#searchForm", "post", "/shopReserve/shopReserveList");
});


/*$(".page-item a").on("click", function(e){
	e.preventDefault();
	$("#searchForm").find("input[name='pageNum']").val($(this).attr("href"));
	actionProcess("#searchForm", "post","/shopReserve/shopReserveList");
});


$(".page-item a").on("click", function(e) {
	e.preventDefault();
	//console.log($(this).data("number"));
	$("#page").val($(this).data("number"));
	actionProcess("#searchForm", "get", "/shopReserve/shopReserveList");
});*/

/* 페이징 처리 - 이벤트 중복 제거 */
$(".pagination .page-link").on("click", function(e) {
    e.preventDefault();
    const pageNum = $(this).data("number");
    $("#page").val(pageNum);
    
    // GET 방식으로 변경
    actionProcess("#searchForm", "get", "/shopReserve/shopReserveListPaged");
});

/* 상세보기 이벤트 */
$(".goDetail").on("click", function() {
    const shopReserveNo = $(this).closest(".card").find("h5:last-child").text();
    locationProcess("/shopReserve/" + shopReserveNo);
});

$("#allSearchBtn").on("click", function () {
	locationProcess("/shopReserve/shopReserveList");
});

$search.on("change", dateSearchDisplay);

dateSearchDisplay();
resetDateFields();
searchKeyword();


/* 제목 클릭시 상세 페이지 이동을 위한 처리 이벤트 		
$(".goDetail").on("click", function(){
	//let boardNumber =  $(this).parents("tr").attr("data-num");	
	let boardNumber =  $(this).parents("tr").data("num");
	console.log("글번호 : " + boardNumber);
	
	locationProcess("/board/"+boardNumber);
});*/
/*$(".goDetail").on("click", function(){	
	let shopReserveNo =  $(this).parents("tr").data("shopReserveNo");
	locationProcess("/shopReserve/"+shopReserveNo);
});*/




/* 제목 클릭시 상세 페이지 이동을 위한 처리 이벤트 	
$(".goDetail").on("click", function(){	
	let no =  $(this).parents("tr").data("shopReserveNo");
	locationProcess("/shopReserve/"+shopReserveNo);
});*/	


	
$("#insertFormBtn").on("click", () => {
	locationProcess("/shopReserve/insertForm");
});
