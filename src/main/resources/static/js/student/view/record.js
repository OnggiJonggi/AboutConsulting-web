/* ────────────────────────────────────────
   record 조각 초기화 함수
   view.js loadFragment 성공 콜백에서 호출
   ──────────────────────────────────────── */
function initRecordFragment() {

  let statusPollTimer = null;

  /* ── 파일 선택 표시 ── */
  const fileInput = document.getElementById('record-file-input');
  if (fileInput) {
    fileInput.addEventListener('change', function () {
      const nameEl = document.getElementById('record-file-name');
      if (nameEl) {
        nameEl.textContent = this.files.length > 0 ? this.files[0].name : '파일 선택';
      }
    });
  }

  /* ── 업로드 버튼 ── */
  const uploadBtn = document.getElementById('record-upload-btn');
  if (uploadBtn) {
    uploadBtn.addEventListener('click', function () {
      const file = fileInput ? fileInput.files[0] : null;
      if (!file) {
        alert('PDF 파일을 선택해 주세요.');
        return;
      }

      const uploadUrl    = this.dataset.url;
      const statusUrl    = this.dataset.statusUrl;
      const reloadUrl    = this.dataset.reloadUrl;

      const formData = new FormData();
      formData.append('file', file);

      $.ajax({
        url: uploadUrl,
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function () {
          showPendingArea();
          startStatusPolling(statusUrl, reloadUrl);
        },
        error: function () {
          alert('업로드 중 오류가 발생했습니다.');
        }
      });
    });
  }

  /* ── READY 상태 : 페이지 진입 즉시 폴링 시작 ── */
  const readyPending = document.getElementById('record-pending-area');
  if (readyPending && readyPending.dataset.statusUrl) {
    startStatusPolling(
      readyPending.dataset.statusUrl,
      readyPending.dataset.reloadUrl
    );
  }

  /* ── 분석 기록 버튼 ── */
  const historyBtn = document.getElementById('record-history-btn');
  if (historyBtn) {
    historyBtn.addEventListener('click', function () {
      const url = this.dataset.url;
      const modal = document.getElementById('record-history-modal');
      const content = document.getElementById('record-history-content');

      content.innerHTML =
        '<div class="record-history-loading">' +
        '<div class="record-pending-card__spinner"></div><span>불러오는 중...</span></div>';
      modal.style.display = 'flex';

      $.ajax({
        url: url,
        method: 'GET',
        success: function (html) {
          content.innerHTML = html;
          lucide.createIcons();
        },
        error: function () {
          content.innerHTML = '<p style="padding:1.5rem;color:#C62828;">기록을 불러오는 중 오류가 발생했습니다.</p>';
        }
      });
    });
  }

  /* ── 분석 기록 모달 닫기 ── */
  document.getElementById('record-history-close') &&
    document.getElementById('record-history-close').addEventListener('click', function () {
      document.getElementById('record-history-modal').style.display = 'none';
    });

  document.getElementById('record-history-modal') &&
    document.getElementById('record-history-modal').addEventListener('click', function (e) {
      if (e.target === this) this.style.display = 'none';
    });

  /* ────── 내부 헬퍼 ────── */

  function showPendingArea() {
    const upload  = document.getElementById('record-upload-area');
    const pending = document.getElementById('record-pending-area');
    const failed  = document.getElementById('record-failed-notice');
    if (upload)  upload.style.display  = 'none';
    if (failed)  failed.style.display  = 'none';
    if (pending) pending.style.display = 'flex';
  }

  function startStatusPolling(statusUrl, reloadUrl) {
    if (statusPollTimer) clearInterval(statusPollTimer);

    statusPollTimer = setInterval(function () {
      $.ajax({
        url: statusUrl,
        method: 'GET',
        statusCode: {
          200: function () {
            clearInterval(statusPollTimer);
            reloadFragment(reloadUrl);
          },
          404: function () {
            /* 아직 준비 안 됨 — 계속 폴링 */
          }
        },
        error: function (xhr) {
          if (xhr.status !== 404) {
            clearInterval(statusPollTimer);
          }
        }
      });
    }, 5000);
  }

  function reloadFragment(reloadUrl) {
    $.ajax({
      url: reloadUrl,
      method: 'GET',
      success: function (html) {
        document.getElementById('detail-content').innerHTML = html;
        lucide.createIcons();
        if (typeof initRecordFragment === 'function') initRecordFragment();
      },
      error: function () {
        alert('분석 결과를 불러오는 중 오류가 발생했습니다.');
      }
    });
  }
}