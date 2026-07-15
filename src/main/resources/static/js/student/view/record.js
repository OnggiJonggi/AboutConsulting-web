(function () {
  'use strict';

  /* ── 폴링 인터벌 핸들 ── */
  let _recordPollInterval = null;

  /* ── 초기화 ── */
  function initRecord() {
    let metaEl = document.getElementById('record-enc-no');
    if (!metaEl) return;

    let encNo = metaEl.dataset.no;
    let status = metaEl.dataset.status;

    let fileInput = document.getElementById('record-file-input');
    if (fileInput) {
      let newInput = fileInput.cloneNode(true);
      fileInput.parentNode.replaceChild(newInput, fileInput);
      newInput.addEventListener('change', function () {
        onFileSelected(this, encNo);
      });
    }

    let uploadBtn = document.getElementById('record-btn-upload');
    if (uploadBtn) {
      let newBtn = uploadBtn.cloneNode(true);
      uploadBtn.parentNode.replaceChild(newBtn, uploadBtn);
      newBtn.addEventListener('click', function () {
        doUpload(encNo);
      });
    }

    if (status === 'READY') {
      let groupNo = sessionStorage.getItem('record_groupNo_' + encNo) || null;
      startPolling(encNo, groupNo);
    }
  }

  /* ── 파일 선택 핸들러 ── */
  function onFileSelected(input, encNo) {
    let errEl = document.getElementById('record-upload-err');
    let uploadBtn = document.getElementById('record-btn-upload');
    let labelText = document.getElementById('record-file-label-text');

    clearError(errEl);

    if (!input.files || input.files.length === 0) {
      if (uploadBtn) uploadBtn.style.display = 'none';
      if (labelText) labelText.textContent = '파일 선택';
      return;
    }

    let file = input.files[0];

    /* PDF 타입 검사 */
    if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
      showError(errEl, 'PDF 파일만 업로드할 수 있습니다.');
      input.value = '';
      if (uploadBtn) uploadBtn.style.display = 'none';
      if (labelText) labelText.textContent = '파일 선택';
      return;
    }

    /* 5 MB 크기 검사 */
    if (file.size > 5 * 1024 * 1024) {
      showError(errEl, '파일 크기는 5MB 이하여야 합니다.');
      input.value = '';
      if (uploadBtn) uploadBtn.style.display = 'none';
      if (labelText) labelText.textContent = '파일 선택';
      return;
    }

    /* 선택 성공 → 파일명 표시 + 업로드 버튼 노출 */
    if (labelText) labelText.textContent = file.name;
    if (uploadBtn) uploadBtn.style.display = 'inline-flex';
  }

  /* ── 업로드 실행 ── */
  function doUpload(encNo) {
    let fileInput = document.getElementById('record-file-input');
    let errEl = document.getElementById('record-upload-err');
    let uploadBtn = document.getElementById('record-btn-upload');

    clearError(errEl);

    if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
      showError(errEl, '파일을 선택해주세요.');
      return;
    }

    let formData = new FormData();
    formData.append('encStudentNo', encNo);
    formData.append('file', fileInput.files[0]);

    if (uploadBtn) uploadBtn.disabled = true;

    $.ajax({
      url: '/api/student/' + encNo + '/record/upload',
      method: 'POST',
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        /* 응답 body에서 encGroupNo 추출 */
        let groupNo = (typeof response === 'object')
          ? response.encGroupNo
          : response;

        /* groupNo 세션스토리지에 저장 (페이지 재로드 후 READY 상태일 때 사용) */
        if (groupNo) {
          sessionStorage.setItem('record_groupNo_' + encNo, groupNo);
        }

        /* 업로드 카드 숨기고 분석 중 카드 노출 */
        hideUploadCard();
        showPendingCard();

        startPolling(encNo, groupNo);
      },
      error: function () {
        if (uploadBtn) uploadBtn.disabled = false;
        showError(errEl, '업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
      }
    });
  }

  /* ── 폴링 시작 ── */
  function startPolling(encNo, groupNo) {
    /* 중복 폴링 방지 */
    if (_recordPollInterval) clearInterval(_recordPollInterval);

    _recordPollInterval = setInterval(function () {
      pollStatus(encNo, groupNo);
    }, 5000);
  }

  /* ── 상태 폴링 요청 ── */
  function pollStatus(encNo, groupNo) {
    let params = groupNo ? { encGroupNo: groupNo } : {};

    $.ajax({
      url: '/api/student/record/status',
      method: 'GET',
      data: params,
      success: function () {
        /* 200 OK → 조각 재로드 */
        clearInterval(_recordPollInterval);
        _recordPollInterval = null;
        sessionStorage.removeItem('record_groupNo_' + encNo);
        reloadFragment(encNo);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          /* 아직 완료 안 됨 → 다음 인터벌까지 대기 */
          return;
        }

        /* 400, 500 등 -> 폴링 중단 후 업로드 초기화 */
        clearInterval(_recordPollInterval);
        _recordPollInterval = null;
        sessionStorage.removeItem('record_groupNo_' + encNo);
        showPollError(encNo);
      }
    });
  }

  /* ── 폴링 오류 처리: 분석 중 카드 숨기고 업로드 카드 복원 ── */
  function showPollError(encNo) {
    /* 분석 중 카드 숨기기 */
    let pendingDynamic = document.getElementById('record-pending-card-dynamic');
    if (pendingDynamic) pendingDynamic.style.display = 'none';

    let pendingStatic = document.getElementById('record-pending-card');
    if (pendingStatic) pendingStatic.style.display = 'none';

    /* 업로드 카드 다시 노출 */
    let uploadCard = document.getElementById('record-upload-card');
    if (uploadCard) uploadCard.style.display = 'flex';

    /* 파일 입력 초기화 */
    let fileInput = document.getElementById('record-file-input');
    if (fileInput) fileInput.value = '';

    let labelText = document.getElementById('record-file-label-text');
    if (labelText) labelText.textContent = '파일 선택';

    let uploadBtn = document.getElementById('record-btn-upload');
    if (uploadBtn) {
      uploadBtn.style.display = 'none';
      uploadBtn.disabled = false;
    }

    /* 오류 메시지 표시 */
    let errEl = document.getElementById('record-upload-err');
    showError(errEl, '오류가 발생했습니다. 다시 업로드해주세요.');
  }

  /* ── 조각 재요청 ── */
  function reloadFragment(encNo) {
    $.ajax({
      url: '/student/' + encNo + '/record',
      method: 'GET',
      success: function (html) {
        let contentEl = document.getElementById('detail-content');
        if (contentEl) {
          contentEl.innerHTML = html;
          lucide.createIcons();
          /* 재삽입 후 다시 초기화 */
          initRecord();
        }
      },
      error: function () {
        alert('분석 결과를 불러오는 중 오류가 발생했습니다.');
      }
    });
  }

  /* ── UI 헬퍼 ── */
  function hideUploadCard() {
    let card = document.getElementById('record-upload-card');
    if (card) card.style.display = 'none';
  }

  function showPendingCard() {
    let card = document.getElementById('record-pending-card-dynamic');
    if (card) card.style.display = 'flex';
    /* 서버 렌더링된 READY 카드도 표시 */
    let staticCard = document.getElementById('record-pending-card');
    if (staticCard) staticCard.style.display = 'flex';
  }

  function showError(el, msg) {
    if (!el) return;
    el.textContent = msg;
    el.style.display = 'block';
  }

  function clearError(el) {
    if (!el) return;
    el.textContent = '';
    el.style.display = 'none';
  }

  /* ── DOM Ready 후 실행 ── */
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initRecord);
  } else {
    initRecord();
  }

  /* Ajax 삽입 후 재초기화를 위해 전역 노출 */
  window.initRecord = initRecord;

})();