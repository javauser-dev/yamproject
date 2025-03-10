/* 수정 버튼 클릭 시 처리 이벤트 */
$("#updateFormBtn").click(function() {
    actionProcess("#dataForm", "post", "/board/updateForm");
});

/* 삭제 버튼 클릭 시 처리 이벤트 */
$("#boardDeleteBtn").click(function() {
    if (confirm("정말 삭제하시겠습니까?")) {
        actionProcess("#dataForm", "post", "/board/boardDelete");
    }
});
