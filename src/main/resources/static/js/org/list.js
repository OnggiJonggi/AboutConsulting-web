document.addEventListener('DOMContentLoaded', function () {

  let currentPage = 1;

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

    if (!group.querySelector('.radio-btn.active')) {
      const allBtn = group.querySelector('[data-value=""]');
      if (allBtn) allBtn.classList.add('active');
    }
  }

  /* ── 폼 파라미터 수집 ── */
  function collectParams(page) {
    const params = { page: page || 1 };
    const form = document.getElementById('searchForm');

    ['name', 'consultantNickname', 'consultantName'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    const statusEl = document.getElementById('f-status');
    if (statusEl && statusEl.value) params['status'] = statusEl.value;

    return params;
  }

  /* ── 소속 카드 HTML 생성 ── */
  function buildOrgCard(org) {
    const statusMap = {
      ACTIVE: { label: '정상', cls: 'status-badge--active' },
      SUSPENDED: { label: '정지', cls: 'status-badge--suspended' },
      DELETED: { label: '삭제', cls: 'status-badge--deleted' }
    };
    const statusInfo = statusMap[org.status] || { label: org.status, cls: '' };

    return '<div class="org-card" data-enc="' + escapeHtml(org.encOrgNo) + '">' +
      // 1행
      '<div class="card-row card-row--primary">' +
      '<span class="card-org-name">' +
      '<i data-lucide="building-2" class="card-icon"></i>' +
      '<span>' + escapeHtml(org.name || '') + '</span>' +
      '</span>' +
      '<span class="card-leader">' +
      '<i data-lucide="crown" class="card-icon-sm"></i>' +
      '<span>' + escapeHtml(org.leaderNickname || '-') + '</span>' +
      '</span>' +
      '</div>' +
      // 2행
      '<div class="card-row card-row--secondary">' +
      '<span class="card-stat" title="소속 컨설턴트 수">' +
      '<i data-lucide="users" class="meta-icon"></i>' +
      '<span>' + (org.consultantCount || 0) + '</span>' +
      '</span>' +
      '<span class="card-stat" title="전체 담당 학생 수">' +
      '<i data-lucide="graduation-cap" class="meta-icon"></i>' +
      '<span>' + (org.studentCount || 0) + '</span>' +
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
      url: '/api/org',
      type: 'GET',
      data: params,
      success: function (result) {
        renderResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderResult({ list: [], totalCount: 0, totalPage: 0 });
        } else {
          console.error('소속 검색 실패', xhr);
          alert('검색 중 오류가 발생했습니다.');
        }
      }
    });
  }

  /* ── 결과 렌더링 ── */
  function renderResult(result) {
    const listEl = document.getElementById('orgList');
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

    listEl.innerHTML = result.list.map(buildOrgCard).join('');

    listEl.querySelectorAll('.org-card').forEach(function (card) {
      card.addEventListener('click', function () {
        var enc = card.getAttribute('data-enc');
        if (enc) location.href = '/org/' + enc;
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

  document.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const p = parseInt(btn.dataset.page, 10);
      if (!isNaN(p)) doSearch(p);
    });
  });

  initStatusRadio();
});