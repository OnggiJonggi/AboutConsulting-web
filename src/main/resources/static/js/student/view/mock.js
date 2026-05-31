(function () {

  /* ── 전역 상태 ── */
  let mockPollingTimer = null;
  let mockEncryptedGroupNo = null;

  /* ─────────────────────────────────────
     초기화 (main.js의 loadFragment가 호출)
  ───────────────────────────────────── */
  window.initMock = function () {
    const fileInput = document.getElementById('mock-file-input');
    const fileSelectBtn = document.getElementById('mock-file-select-btn');
    const uploadBtn = document.getElementById('mock-upload-btn');
    const fileNameSpan = document.getElementById('mock-file-name');

    if (!fileSelectBtn) return;

    /* 파일 선택 버튼 → input[file] 클릭 */
    fileSelectBtn.addEventListener('click', function () {
      if (fileInput) fileInput.click();
    });

    /* 파일 선택 시 */
    if (fileInput) {
      fileInput.addEventListener('change', function () {
        const file = fileInput.files[0];

        mockHideMsg();
        uploadBtn.classList.add('mock-upload-btn--hidden');
        if (fileNameSpan) fileNameSpan.textContent = '';

        if (!file) return;

        /* 5 MB 초과 검사 */
        if (file.size > 5 * 1024 * 1024) {
          mockShowMsg('5MB 이하의 PDF 파일만 업로드할 수 있습니다.', 'error');
          fileInput.value = '';
          return;
        }

        if (fileNameSpan) fileNameSpan.textContent = file.name;
        uploadBtn.classList.remove('mock-upload-btn--hidden');
      });
    }

    /* 업로드 버튼 클릭 */
    if (uploadBtn) {
      uploadBtn.addEventListener('click', function () {
        const file = fileInput && fileInput.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        mockHideMsg();
        uploadBtn.disabled = true;

        $.ajax({
          url: '/api/student/' + ENCRYPTED_NO + '/mock/upload',
          method: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: function (responseBody) {
            mockEncryptedGroupNo = responseBody;
            uploadBtn.classList.add('mock-upload-btn--hidden');
            if (fileNameSpan) fileNameSpan.textContent = '';
            if (fileInput) fileInput.value = '';
            mockStartAnalyzing();
          },
          error: function (xhr) {
            uploadBtn.disabled = false;
            if (xhr.status === 400) {
              mockShowMsg('잘못된 파일입니다.', 'error');
            } else {
              mockShowMsg('업로드에 실패했습니다.', 'error');
            }
          }
        });
      });
    }
  };

  /* ─────────────────────────────────────
     분석 중 UI 표시 + 폴링 시작
  ───────────────────────────────────── */
  function mockStartAnalyzing() {
    const analyzingEl = document.getElementById('mock-analyzing');
    if (analyzingEl) analyzingEl.style.display = 'flex';

    mockStopPolling();
    mockPollingTimer = setInterval(mockPollStatus, 5000);
  }

  /* ─────────────────────────────────────
     폴링 : 분석 완료 여부 확인
  ───────────────────────────────────── */
  function mockPollStatus() {
    $.ajax({
      url: '/api/student/mock/status',
      method: 'GET',
      data: { encryptedGroupNo: mockEncryptedGroupNo },
      success: function () {
        /* 200 → 조각 재요청 */
        mockStopPolling();
        loadMockFragment();
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          /* 아직 처리 중 → 다음 폴링 대기 */
          return;
        }
        /* 400 또는 기타 에러 */
        mockStopPolling();
        mockResetUpload('분석에 실패했습니다. 다시 업로드해주세요.');
      }
    });
  }

  /* ─────────────────────────────────────
     조각 재요청 (분석 완료 후)
  ───────────────────────────────────── */
  function loadMockFragment() {
    $.ajax({
      url: '/student/' + ENCRYPTED_NO + '/mock',
      method: 'GET',
      success: function (html) {
        document.getElementById('detail-content').innerHTML = html;
        lucide.createIcons();
        if (typeof window.initMock === 'function') window.initMock();
      },
      error: function () {
        mockResetUpload('페이지를 불러오는 중 오류가 발생했습니다.');
      }
    });
  }

  /* ─────────────────────────────────────
     업로드 영역 초기화 (실패 시)
  ───────────────────────────────────── */
  function mockResetUpload(message) {
    const analyzingEl = document.getElementById('mock-analyzing');
    if (analyzingEl) analyzingEl.style.display = 'none';
    mockEncryptedGroupNo = null;
    mockShowMsg(message, 'error');

    const uploadBtn = document.getElementById('mock-upload-btn');
    if (uploadBtn) {
      uploadBtn.disabled = false;
      uploadBtn.classList.add('mock-upload-btn--hidden');
    }
    const fileInput = document.getElementById('mock-file-input');
    if (fileInput) fileInput.value = '';
    const fileNameSpan = document.getElementById('mock-file-name');
    if (fileNameSpan) fileNameSpan.textContent = '';
  }

  /* ─────────────────────────────────────
     폴링 중지
  ───────────────────────────────────── */
  function mockStopPolling() {
    if (mockPollingTimer) {
      clearInterval(mockPollingTimer);
      mockPollingTimer = null;
    }
  }

  /* ─────────────────────────────────────
     메시지 헬퍼
  ───────────────────────────────────── */
  function mockShowMsg(text, type) {
    const el = document.getElementById('mock-upload-msg');
    if (!el) return;
    el.textContent = text;
    el.className = 'mock-upload-msg mock-upload-msg--' + (type === 'error' ? 'error' : 'info');
    el.style.display = 'block';
  }

  function mockHideMsg() {
    const el = document.getElementById('mock-upload-msg');
    if (el) el.style.display = 'none';
  }

  /* ─────────────────────────────────────
     카드 버튼 전역 함수
  ───────────────────────────────────── */

  /* 원본 확인 */
  window.mockViewOriginal = function (btn) {
    const encNo = btn.dataset.encryptedMockNo;
    $.ajax({
      url: '/api/student/mock/original',
      method: 'GET',
      data: { encryptedMockNo: encNo },
      xhrFields: { responseType: 'blob' },
      success: function (blob, status, xhr) {
        const contentDisposition = xhr.getResponseHeader('Content-Disposition') || '';
        const filenameMatch = contentDisposition.match(/filename\*?=(?:UTF-8'')?["']?([^;"'\n]+)/i);
        const filename = filenameMatch ? decodeURIComponent(filenameMatch[1]) : 'original.pdf';
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
      },
      error: function () {
        alert('원본 파일을 불러오는 중 오류가 발생했습니다.');
      }
    });
  };

  /* 점수 수정 */
  window.mockUpdate = function (btn) {
    const encNo = btn.dataset.encryptedMockNo;
    $.ajax({
      url: '/api/student/mock/update',
      method: 'PUT',
      contentType: 'application/json',
      data: JSON.stringify({ encryptedMockNo: encNo }),
      success: function () {
        loadMockFragment();
      },
      error: function () {
        alert('점수 수정 중 오류가 발생했습니다.');
      }
    });
  };

  /* 원본 삭제 */
  window.mockDelete = function (btn) {
    if (!confirm('이 모의고사 원본을 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.')) return;
    const encNo = btn.dataset.encryptedMockNo;
    $.ajax({
      url: '/api/student/mock/delete',
      method: 'DELETE',
      data: { encryptedMockNo: encNo },
      success: function () {
        loadMockFragment();
      },
      error: function () {
        alert('삭제 중 오류가 발생했습니다.');
      }
    });
  };

})();