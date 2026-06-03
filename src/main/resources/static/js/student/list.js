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

    ['name', 'schoolName'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    ['grade', 'semester'].forEach(function (key) {
      const el = form.querySelector('#f-' + key);
      if (el && el.value) params[key] = el.value;
    });

    const trackEl = form.querySelector('#f-track');
    if (trackEl && trackEl.value) params['track'] = trackEl.value;

    const targetUniv = form.querySelector('[name="target.univ"]');
    const targetMajor = form.querySelector('[name="target.major"]');
    if (targetUniv && targetUniv.value.trim()) params['target.univ'] = targetUniv.value.trim();
    if (targetMajor && targetMajor.value.trim()) params['target.major'] = targetMajor.value.trim();

    return params;
  }

  /* ── 학생 카드 HTML 생성 ── */
  function buildStudentCard(student) {
    const targets = (student.target || [])
      .slice()
      .sort((a, b) => a.ranking - b.ranking);

    const targetsHtml = targets.length > 0
      ? '<div class="student-targets">' +
      targets.map(function (t) {
        return '<div class="target-item">' +
          '<span class="target-rank">' + t.ranking + '지망</span>' +
          '<i data-lucide="landmark" class="meta-icon"></i>' +
          '<span class="target-univ">' + escapeHtml(t.univ || '') + '</span>' +
          '<span class="meta-sep">|</span>' +
          '<i data-lucide="flask-conical" class="meta-icon"></i>' +
          '<span class="target-major">' + escapeHtml(t.major || '') + '</span>' +
          '</div>';
      }).join('') +
      '</div>'
      : '';

    return '<div class="student-card" onclick="location.href=\'/student/' + escapeHtml(student.encryptedStudentNo) + '\'">' +
      '<div class="student-main">' +
      '<div class="student-name-row">' +
      '<i data-lucide="user-round" class="student-icon"></i>' +
      '<span class="student-name">' + escapeHtml(student.name || '') + '</span>' +
      '</div>' +
      '<div class="student-meta">' +
      '<span class="meta-item"><i data-lucide="school" class="meta-icon"></i>' + escapeHtml(student.schoolName || '') + '</span>' +
      '<span class="meta-sep">|</span>' +
      '<span class="meta-item"><i data-lucide="layers" class="meta-icon"></i>' + student.grade + '학년</span>' +
      '<span class="meta-sep">|</span>' +
      '<span class="meta-item"><i data-lucide="calendar" class="meta-icon"></i>' + student.semester + '학기</span>' +
      '<span class="meta-sep">|</span>' +
      '<span class="meta-item"><i data-lucide="book-open" class="meta-icon"></i>' + escapeHtml(student.track || '') + '</span>' +
      '</div>' +
      '</div>' +
      targetsHtml +
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
      url: '/api/student',
      type: 'GET',
      data: params,
      success: function (result) {
        renderResult(result);
      },
      error: function (xhr) {
        console.error('학생 검색 실패', xhr);
        alert('검색 중 오류가 발생했습니다.');
      }
    });
  }

  /* ── 결과 렌더링 ── */
  function renderResult(result) {
    const listEl = document.getElementById('studentList');
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

    listEl.innerHTML = result.list.map(buildStudentCard).join('');
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