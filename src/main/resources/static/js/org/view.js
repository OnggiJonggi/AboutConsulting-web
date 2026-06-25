/* ─────────────────────────────────────────
   소속명 수정
───────────────────────────────────────── */
const NAME_MAX_LENGTH = 20;
const NAME_REGEXP = /^[ㄱ-ㅎ가-힣a-zA-Z0-9.\-_()\s@!?,~+=*#]{1,20}$/;

let nameCheckPassed = false;

// 소속명 수정 영역 열기/닫기
document.getElementById('btnOpenNameEdit')?.addEventListener('click', function () {
  const area = document.getElementById('nameEditArea');
  const isHidden = area.style.display === 'none';
  area.style.display = isHidden ? 'block' : 'none';
  if (isHidden) {
    document.getElementById('inputOrgName').focus();
    resetNameEdit();
  }
});

// 입력값 변경 시 중복 확인 초기화
document.getElementById('inputOrgName')?.addEventListener('input', function () {
  resetNameEdit();
});

function resetNameEdit() {
  nameCheckPassed = false;
  const msgEl = document.getElementById('nameEditMsg');
  msgEl.textContent = '';
  msgEl.className = 'edit-msg';
  document.getElementById('btnSubmitName').style.display = 'none';
}

// 중복 확인
document.getElementById('btnCheckName')?.addEventListener('click', function () {
  const name = document.getElementById('inputOrgName').value.trim();
  const msgEl = document.getElementById('nameEditMsg');

  if (!NAME_REGEXP.test(name)) {
    msgEl.textContent = '소속명은 1~20자의 허용 문자만 사용 가능합니다.';
    msgEl.className = 'edit-msg err';
    nameCheckPassed = false;
    document.getElementById('btnSubmitName').style.display = 'none';
    return;
  }

  $.ajax({
    url: '/api/org/check-name',
    type: 'GET',
    data: { name: name },
    statusCode: {
      200: function () {
        msgEl.textContent = '사용 가능한 소속명입니다.';
        msgEl.className = 'edit-msg ok';
        nameCheckPassed = true;
        document.getElementById('btnSubmitName').style.display = 'inline-flex';
        lucide.createIcons();
      }
    },
    error: function (xhr) {
      if (xhr.status === 200) {
        msgEl.textContent = '사용 가능한 소속명입니다.';
        msgEl.className = 'edit-msg ok';
        nameCheckPassed = true;
        document.getElementById('btnSubmitName').style.display = 'inline-flex';
        lucide.createIcons();
      } else if (xhr.status === 409) {
        msgEl.textContent = '이미 사용 중인 소속명입니다.';
        msgEl.className = 'edit-msg err';
        nameCheckPassed = false;
        document.getElementById('btnSubmitName').style.display = 'none';
      } else {
        msgEl.textContent = '확인 중 오류가 발생했습니다. (status: ' + xhr.status + ')';
        msgEl.className = 'edit-msg err';
        nameCheckPassed = false;
        document.getElementById('btnSubmitName').style.display = 'none';
      }
    }
  });
});

// 소속명 수정 요청
document.getElementById('btnSubmitName')?.addEventListener('click', function () {
  if (!nameCheckPassed) return;

  const name = document.getElementById('inputOrgName').value.trim();

  $.ajax({
    url: '/api/org/' + ENC_ORG_NO + '/name',
    type: 'PUT',
    data: { name: name },
    statusCode: {
      200: function () {
        location.reload();
      }
    },
    error: function (xhr) {
      if (xhr.status === 200) {
        location.reload();
      } else {
        alert('소속명 수정에 실패했습니다. (status: ' + xhr.status + ')');
      }
    }
  });
});


/* ─────────────────────────────────────────
   상태 수정
───────────────────────────────────────── */

// 상태 수정 영역 열기
document.getElementById('btnOpenStatusEdit')?.addEventListener('click', function () {
  const area = document.getElementById('statusEditArea');
  area.style.display = area.style.display === 'none' ? 'flex' : 'none';
});

// 취소
document.getElementById('btnCancelStatus')?.addEventListener('click', function () {
  document.getElementById('statusEditArea').style.display = 'none';
});

// 수정 완료
document.getElementById('btnSubmitStatus')?.addEventListener('click', function () {
  const status = document.getElementById('selectStatus').value;

  $.ajax({
    url: '/api/org/' + ENC_ORG_NO + '/status',
    type: 'PUT',
    data: { status: status },
    statusCode: {
      200: function () {
        location.reload();
      }
    },
    error: function (xhr) {
      if (xhr.status === 200) {
        location.reload();
      } else {
        alert('상태 수정에 실패했습니다. (status: ' + xhr.status + ')');
      }
    }
  });
});


/* ─────────────────────────────────────────
   컨설턴트 상세 페이지 이동
───────────────────────────────────────── */
function goConsultant(el) {
  const enc = el.getAttribute('data-enc');
  location.href = '/consultant/' + enc;
}