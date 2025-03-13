/* 상세 클릭 시 상세 페이지 이동을 위한 처리 이벤트 */
// $(".goDetail").on("click", function() {
//     let no = $(this).parents("tr").data("no");
//     locationProcess("/board/" + no);
// });

/* 페이징 처리 */
$(".page-item").on("click", function(e) {
    e.preventDefault(); // 기본 이벤트 방지

    // console.log($(this).data("number")); // 디버깅용 (필요 시 활성화)
    $("#page").val($(this).data("number")); // 페이지 번호 설정
    actionProcess("#searchForm", "get", "/board/boardList"); // 검색 폼 전송 (GET 방식)
});
