/* ── 학생 상세 페이지 이동 ── */
function goStudent(el) {
  const enc = el.getAttribute('data-enc');
  location.href = '/student/' + enc;
}

/* ── 담당 학생 삭제 ── */
function deleteCharged(event, btn) {
  // 카드 클릭(goStudent)으로 이벤트가 전파되지 않도록 중단
  event.stopPropagation();

  const card = btn.closest('.student-card');
  const encStudentNo = card.getAttribute('data-enc');
  const encConsultantNo = card.getAttribute('data-enc-con') || ENCRYPTED_CONSULTANT_NO;

  if (!confirm('이 학생을 담당 목록에서 삭제하시겠습니까?')) return;

  $.ajax({
    url: '/api/consultant/' + encConsultantNo + '/charged',
    type: 'DELETE',
    data: { encStudentNo: encStudentNo },
    statusCode: {
      200: function () {
        location.reload();
      }
    },
    error: function (xhr) {
      if (xhr.status === 200) {
        location.reload();
      } else {
        alert('삭제에 실패했습니다. (status: ' + xhr.status + ')');
      }
    }
  });
}