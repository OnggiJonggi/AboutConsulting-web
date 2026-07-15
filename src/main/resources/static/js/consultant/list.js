document.addEventListener('DOMContentLoaded', function () {

  let currentPage = 1;

  // inCharged 상태 추적
  // null: 미설정(파라미터 미전송), true: 담당있음, false: 담당없음
  let inChargedState = null;

  /* ── 상태 라디오 버튼 초기화 ── */
  function initStatusRadio() {
    const group = document.getElementById('statusGroup');
    if (!group) return;
    const buttons = group.querySelectorAll('.radio-btn');
    const hiddenInput = document.getElementById('f-status');
    const currentVal = hiddenInput ? hiddenInput.value : '';

    buttons.forEach(function (btn) {
      if (btn.dataset.value === currentVal) {
        btn.classList.add('active');
      }
      btn.addEventListener('click', function () {
        buttons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        if (hiddenInput) hiddenInput.value = btn.dataset.value;
      });
    });

    // 초기값 없으면 '전체' 활성화
    if (!group.querySelector('.radio-btn.active')) {
      const allBtn = group.querySelector('[data-value=""]');
      if (allBtn) allBtn.classList.add('active');
    }
  }

  /* ── '담당 학생 없음' 체크박스 처리 ── */
  function initNoStudentCheck() {
    const checkbox = document.getElementById('f-noStudent');
    const startInput = document.getElementById('f-chargedCountStart');
    const endInput = document.getElementById('f-chargedCountEnd');

    if (!checkbox) return;

    checkbox.addEventListener('change', function () {
      if (checkbox.checked) {
        // 담당 학생 없음 선택 → inCharged=false, 범위 입력 비활성화
        inChargedState = false;
        startInput.disabled = true;
        endInput.disabled = true;
        startInput.value = '';
        endInput.value = '';
      } else {
        // 체크 해제 → 범위 입력 활성화, inCharged는 입력 여부에 따라 결정
        startInput.disabled = false;
        endInput.disabled = false;
        // 체크 해제 시 범위 입력이 비어있으면 미설정
        const hasRange = startInput.value.trim() !== '' || endInput.value.trim() !== '';
        inChargedState = hasRange ? true : null;
      }
    });

    // 범위 입력 변경 시 inCharged=true 설정
    [startInput, endInput].forEach(function (input) {
      input.addEventListener('input', function () {
        if (checkbox.checked) return; // 체크된 상태면 무시
        const hasRange = startInput.value.trim() !== '' || endInput.value.trim() !== '';
        inChargedState = hasRange ? true : null;
      });
    });
  }

  /* ── 폼 파라미터 수집 ── */
  function collectParams(page) {
    const params = { page: page || 1 };
    const form = document.getElementById('searchForm');

    // 텍스트 입력 필드
    ['name', 'nickname', 'studentName', 'orgName'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    // 상태
    const statusEl = document.getElementById('f-status');
    if (statusEl && statusEl.value) params['status'] = statusEl.value;

    // 담당 학생 수 범위 (비활성화된 경우 전송 안 함)
    const startEl = document.getElementById('f-chargedCountStart');
    const endEl = document.getElementById('f-chargedCountEnd');
    if (startEl && !startEl.disabled && startEl.value.trim() !== '') {
      params['chargedCountStart'] = startEl.value.trim();
    }
    if (endEl && !endEl.disabled && endEl.value.trim() !== '') {
      params['chargedCountEnd'] = endEl.value.trim();
    }

    // inCharged: null이면 파라미터 미포함
    if (inChargedState !== null) {
      params['inCharged'] = String(inChargedState);
    }

    return params;
  }

  /* ── 컨설턴트 카드 HTML 생성 ── */
  function buildConsultantCard(consultant) {
    const statusMap = {
      ACTIVE: { label: '정상', cls: 'status-badge--active' },
      SUSPENDED: { label: '정지', cls: 'status-badge--suspended' },
      DELETED: { label: '삭제', cls: 'status-badge--deleted' }
    };
    const statusInfo = statusMap[consultant.status] || { label: consultant.status, cls: '' };

    return '<div class="consultant-card" data-enc="' + escapeHtml(consultant.encConsultantNo) + '">' +
      // 1행
      '<div class="card-row card-row--primary">' +
      '<span class="card-nickname">' +
      '<i data-lucide="contact" class="card-icon"></i>' +
      '<span>' + escapeHtml(consultant.nickname || '') + '</span>' +
      '</span>' +
      '<span class="card-org">' +
      '<i data-lucide="building-2" class="card-icon-sm"></i>' +
      '<span>' + escapeHtml(consultant.orgName || '-') + '</span>' +
      '</span>' +
      '<span class="card-student-count">' +
      '<i data-lucide="users-round" class="card-icon-sm"></i>' +
      '<span>' + (consultant.studentCount || 0) + '명</span>' +
      '</span>' +
      '</div>' +
      // 2행
      '<div class="card-row card-row--secondary">' +
      '<span class="card-userid">' +
      '<i data-lucide="id-card" class="meta-icon"></i>' +
      '<span>' + escapeHtml(consultant.userId || '') + '</span>' +
      '</span>' +
      '<span class="card-name">' +
      '<i data-lucide="user-round" class="meta-icon"></i>' +
      '<span>' + escapeHtml(consultant.name || '') + '</span>' +
      '</span>' +
      '<span class="status-badge ' + statusInfo.cls + '">' + statusInfo.label + '</span>' +
      '</div>' +
      '</div>';
  }

  /* ── 페이징 HTML 생성 ── */
  function buildPagination(data) {
    const area = document.getElementById('paginationArea');
    if (!area) return;

    if (!data.totalPage || data.totalPage === 0) {
      area.style.display = 'none';
      return;
    }
    area.style.display = 'flex';

    const prevClass = data.hasPrev ? '' : 'hidden';
    const nextClass = data.hasNext ? '' : 'hidden';

    let pageNums = '';
    for (let p = data.startPage; p <= data.endPage; p++) {
      const activeClass = p === currentPage ? ' active' : '';
      pageNums += '<button type="button" class="page-num' + activeClass + '" data-page="' + p + '">' + p + '</button>';
    }

    area.innerHTML =
      '<div class="pagination">' +
      '<button type="button" class="page-nav ' + prevClass + '" id="prevBlock" data-page="' + (data.startPage - 1) + '">' +
      '<i data-lucide="chevron-left"></i>' +
      '</button>' +
      '<div class="page-numbers">' + pageNums + '</div>' +
      '<button type="button" class="page-nav ' + nextClass + '" id="nextBlock" data-page="' + (data.endPage + 1) + '">' +
      '<i data-lucide="chevron-right"></i>' +
      '</button>' +
      '</div>';

    area.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p)) doSearch(p);
      });
    });

    lucide.createIcons();
  }

  /* ── AJAX 검색 실행 ── */
  function doSearch(page) {
    currentPage = page || 1;
    const params = collectParams(currentPage);

    $.ajax({
      url: '/api/consultant',
      type: 'GET',
      data: params,
      success: function (result) {
        renderResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderResult({ list: [], totalCount: 0, totalPage: 0 });
        } else {
          console.error('컨설턴트 검색 실패', xhr);
          alert('검색 중 오류가 발생했습니다.');
        }
      }
    });
  }

  /* ── 결과 렌더링 ── */
  function renderResult(result) {
    const listEl = document.getElementById('consultantList');
    const totalCountEl = document.getElementById('totalCount');

    if (totalCountEl) totalCountEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      listEl.innerHTML =
        '<div class="empty-msg" id="emptyMsg">' +
        '<i data-lucide="search-x" class="empty-icon"></i>' +
        '<p>검색 결과가 없습니다.</p>' +
        '</div>';
      lucide.createIcons();
      buildPagination({ totalPage: 0 });
      return;
    }

    listEl.innerHTML = result.list.map(buildConsultantCard).join('');

    // ── 카드 클릭 이벤트 바인딩 ──
    listEl.querySelectorAll('.consultant-card').forEach(function (card) {
      card.addEventListener('click', function () {
        var enc = card.getAttribute('data-enc');
        if (enc) location.href = '/consultant/' + enc;
      });
    });

    lucide.createIcons();
    buildPagination(result);
  }

  /* ── HTML 이스케이프 ── */
  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  /* ── 이벤트 바인딩 ── */
  document.getElementById('searchBtn').addEventListener('click', function () {
    doSearch(1);
  });

  // 페이지 로드 시 서버 렌더링된 페이징 버튼에도 이벤트 부여
  document.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const p = parseInt(btn.dataset.page, 10);
      if (!isNaN(p)) doSearch(p);
    });
  });

  initStatusRadio();
  initNoStudentCheck();
});