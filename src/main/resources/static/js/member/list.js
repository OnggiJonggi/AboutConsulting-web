document.addEventListener('DOMContentLoaded', function () {

  let currentPage = 1;

  /* ── 라디오 버튼 그룹 초기화 ── */
  function initRadioGroups() {
    document.querySelectorAll('.btn-group-radio').forEach(function (group) {
      const buttons = group.querySelectorAll('.radio-btn');
      const inputName = buttons[0]?.dataset.name;
      if (!inputName) return;

      const hiddenInput = document.getElementById('f-' + inputName);
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

      // 초기값이 없으면 '전체' 활성화
      const anyActive = group.querySelector('.radio-btn.active');
      if (!anyActive) {
        const allBtn = group.querySelector('[data-value=""]');
        if (allBtn) allBtn.classList.add('active');
      }
    });
  }

  /* ── 폼 파라미터 수집 ── */
  function collectParams(page) {
    const params = { page: page || 1 };
    const form = document.getElementById('searchForm');

    ['userId', 'name', 'nickname', 'phone'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    const roleEl = document.getElementById('f-role');
    const statusEl = document.getElementById('f-status');
    if (statusEl && statusEl.value) params['status'] = statusEl.value;
    if (roleEl && roleEl.value) params['role'] = roleEl.value;

    return params;
  }

  /* ── ROLE_ prefix 제거 ── */
  function stripRole(role) {
    return (role || '').replace(/^ROLE_/, '');
  }

  /* ── 회원 카드 HTML 생성 ── */
  function buildMemberCard(member) {
    const rolesHtml = (member.role || [])
      .map(function (r) {
        return '<span class="role-badge">' + escapeHtml(stripRole(r)) + '</span>';
      })
      .join('');

    const phone = (member.phone && member.phone.trim()) ? member.phone : '-';

    const statusMap = {
      'ACTIVE': 'status--active',
      'DELETED': 'status--deleted',
      'SUSPENDED': 'status--suspended'
    };
    const statusClass = statusMap[member.status] || '';
    const statusHtml = member.status
      ? '<span class="status-badge ' + statusClass + '">' + escapeHtml(member.status) + '</span>'
      : '';

    return '<div class="member-card" onclick="location.href=\'/member/' + escapeHtml(member.encMemberNo) + '\'">' +
      '<div class="member-main">' +
      '<div class="member-id-row">' +
      '<i data-lucide="user-round" class="member-icon"></i>' +
      '<span class="member-userid">' + escapeHtml(member.userId || '') + '</span>' +
      '<span class="member-name">' + escapeHtml(member.name || '') + '</span>' +
      '</div>' +
      '<div class="member-meta">' +
      '<span class="meta-item"><i data-lucide="smile" class="meta-icon"></i>' + escapeHtml(member.nickname || '') + '</span>' +
      '<span class="meta-sep">|</span>' +
      '<span class="meta-item"><i data-lucide="phone" class="meta-icon"></i>' + escapeHtml(phone) + '</span>' +
      '<span class="meta-sep">|</span>' +
      statusHtml +
      '</div>' +
      '</div>' +
      '<div class="member-roles">' + rolesHtml + '</div>' +
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
      url: '/api/member',
      type: 'GET',
      data: params,
      success: function (result) {
        renderResult(result);
      },
      error: function (xhr) {
        console.error('회원 검색 실패', xhr);
        alert('검색 중 오류가 발생했습니다.');
      }
    });
  }

  /* ── 결과 렌더링 ── */
  function renderResult(result) {
    const listEl = document.getElementById('memberList');
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

    listEl.innerHTML = result.list.map(buildMemberCard).join('');
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

  initRadioGroups();
});