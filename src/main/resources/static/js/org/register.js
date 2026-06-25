document.addEventListener('DOMContentLoaded', function () {



  /* ══════════════════════════════════
     상수 / 상태
  ══════════════════════════════════ */
  const NAME_REGEXP = /^[ㄱ-ㅎ가-힣a-zA-Z0-9.\-_()\u0040!?,~+=*#\s]{1,20}$/;

  let currentPage = 1;
  let nameChecked = false;   // 중복 검사 통과 여부
  let leaderEnc = null;      // 현재 대표 컨설턴트 enc

  // 선택된 컨설턴트 맵: enc → { nickname, name, userId, studentCount }
  const selectedMap = new Map();

  /* ══════════════════════════════════
     DOM 참조
  ══════════════════════════════════ */
  const orgNameInput = document.getElementById('orgName');
  const checkNameBtn = document.getElementById('checkNameBtn');
  const nameMsg = document.getElementById('nameMsg');
  const submitBtn = document.getElementById('submitBtn');
  const searchBtn = document.getElementById('searchBtn');
  const consultantListEl = document.getElementById('consultantList');
  const totalCountEl = document.getElementById('totalCount');
  const paginationArea = document.getElementById('paginationArea');
  const paginationInner = document.getElementById('paginationInner');
  const selectedListEl = document.getElementById('selectedList');
  const selectedCountEl = document.getElementById('selectedCount');
  const selectedEmpty = document.getElementById('selectedEmpty');
  const fPage = document.getElementById('f-page');
  const registerForm = document.getElementById('registerForm');
  const fOrgNameSubmit = document.getElementById('f-orgNameSubmit');
  const fEncLeaderNo = document.getElementById('f-encLeaderNo');
  const encConsultantNosContainer = document.getElementById('encConsultantNosContainer');

  /* ══════════════════════════════════
     검색 결과 카드 클릭 — 이벤트 위임
     (초기 렌더링 / AJAX 렌더링 모두 동작)
  ══════════════════════════════════ */
  consultantListEl.addEventListener('click', function (e) {
    const card = e.target.closest('.consultant-card');
    if (!card) return;
    if (card.classList.contains('selected')) return;

    const enc = card.getAttribute('data-enc');
    if (!enc || selectedMap.has(enc)) return;

    const nickname = card.querySelector('.card-nickname span:last-child')?.textContent || '';
    const name = card.querySelector('.card-name span:last-child')?.textContent || '';
    const userId = card.querySelector('.card-userid span:last-child')?.textContent || '';
    const countTxt = card.querySelector('.card-student-count span:last-child')?.textContent || '0명';
    const studentCount = parseInt(countTxt) || 0;

    addSelected(enc, { nickname, name, userId, studentCount });
    card.classList.add('selected');
    card.style.pointerEvents = 'none';
  });

  /* ══════════════════════════════════
     유틸
  ══════════════════════════════════ */
  function escapeHtml(str) {
    return String(str || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function updateSubmitBtn() {
    const ok = nameChecked && leaderEnc !== null;
    submitBtn.disabled = !ok;
  }

  /* ══════════════════════════════════
     소속 이름 중복 검사
  ══════════════════════════════════ */
  orgNameInput.addEventListener('input', function () {
    nameChecked = false;
    updateSubmitBtn();
    nameMsg.textContent = '';
    nameMsg.className = 'name-msg';
  });

  checkNameBtn.addEventListener('click', function () {
    const val = orgNameInput.value.trim();
    if (!val) {
      nameMsg.textContent = '이름을 입력해주세요.';
      nameMsg.className = 'name-msg error';
      return;
    }
    if (!NAME_REGEXP.test(val)) {
      nameMsg.textContent = '이름 형식이 올바르지 않습니다.';
      nameMsg.className = 'name-msg error';
      return;
    }

    $.ajax({
      url: '/api/org/check-name',
      type: 'GET',
      data: { name: val },
      success: function () {
        nameChecked = true;
        nameMsg.textContent = '사용 가능한 이름입니다.';
        nameMsg.className = 'name-msg success';
        updateSubmitBtn();
      },
      error: function () {
        nameChecked = false;
        nameMsg.textContent = '이미 사용 중인 이름입니다.';
        nameMsg.className = 'name-msg error';
        updateSubmitBtn();
      }
    });
  });

  /* ══════════════════════════════════
     검색 결과 카드 HTML 생성
  ══════════════════════════════════ */
  function buildSearchCard(c) {
    const isSelected = selectedMap.has(c.encryptedConsultantNo);
    const selectedCls = isSelected ? ' selected' : '';
    return '<div class="consultant-card' + selectedCls + '" data-enc="' + escapeHtml(c.encryptedConsultantNo) + '">' +
      '<div class="card-row card-row--primary">' +
      '<span class="card-nickname">' +
      '<i data-lucide="contact" class="card-icon"></i>' +
      '<span>' + escapeHtml(c.nickname) + '</span>' +
      '</span>' +
      '<span class="card-student-count">' +
      '<i data-lucide="users-round" class="card-icon-sm"></i>' +
      '<span>' + (c.studentCount || 0) + '명</span>' +
      '</span>' +
      '</div>' +
      '<div class="card-row card-row--secondary">' +
      '<span class="card-userid">' +
      '<i data-lucide="id-card" class="meta-icon"></i>' +
      '<span>' + escapeHtml(c.userId) + '</span>' +
      '</span>' +
      '<span class="card-name">' +
      '<i data-lucide="user-round" class="meta-icon"></i>' +
      '<span>' + escapeHtml(c.name) + '</span>' +
      '</span>' +
      '</div>' +
      '</div>';
  }

  /* ══════════════════════════════════
     검색 결과 렌더링 + 카드 이벤트 바인딩
  ══════════════════════════════════ */
  function renderSearchResult(result) {
    totalCountEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      consultantListEl.innerHTML =
        '<div class="empty-msg">' +
        '<i data-lucide="search-x" class="empty-icon"></i>' +
        '<p>검색 결과가 없습니다.</p>' +
        '</div>';
      lucide.createIcons();
      buildPagination({ totalPage: 0 });
      return;
    }

    consultantListEl.innerHTML = result.list.map(buildSearchCard).join('');
    lucide.createIcons();
    buildPagination(result);
  }


  /* ══════════════════════════════════
     선택된 컨설턴트 추가
  ══════════════════════════════════ */
  function addSelected(enc, data) {
    if (selectedMap.has(enc)) return;
    selectedMap.set(enc, data);

    // 대표가 없으면 자동 설정
    if (leaderEnc === null) {
      leaderEnc = enc;
    }

    renderSelectedList();
    updateSubmitBtn();
  }

  /* ══════════════════════════════════
     선택된 컨설턴트 목록 렌더링
  ══════════════════════════════════ */
  function renderSelectedList() {
    // 빈 메시지 제거 후 재구성
    selectedListEl.innerHTML = '';
    selectedCountEl.textContent = selectedMap.size;

    if (selectedMap.size === 0) {
      selectedListEl.appendChild(selectedEmpty);
      selectedEmpty.style.display = 'flex';
      leaderEnc = null;
      updateSubmitBtn();
      return;
    }

    selectedMap.forEach(function (data, enc) {
      const isLeader = enc === leaderEnc;
      const card = document.createElement('div');
      card.className = 'selected-card' + (isLeader ? ' is-leader' : '');
      card.dataset.enc = enc;

      card.innerHTML =
        '<div class="selected-card__top">' +
        '<div class="selected-card__name-row">' +
        '<i data-lucide="contact" class="card-icon"></i>' +
        '<span>' + escapeHtml(data.nickname) + '</span>' +
        (isLeader ? '<span class="leader-badge"><i data-lucide="crown"></i>대표</span>' : '') +
        '</div>' +
        '<div class="selected-card__actions">' +
        '<button type="button" class="icon-btn icon-btn--leader' + (isLeader ? ' active' : '') + '" title="대표 컨설턴트로 설정">' +
        '<i data-lucide="crown"></i>' +
        '<span class="tooltip">대표 컨설턴트로 설정</span>' +
        '</button>' +
        '<button type="button" class="icon-btn icon-btn--danger" title="삭제">' +
        '<i data-lucide="x"></i>' +
        '</button>' +
        '</div>' +
        '</div>' +
        '<div class="selected-card__meta">' +
        '<i data-lucide="user-round" class="meta-icon"></i>' +
        '<span>' + escapeHtml(data.name) + '</span>' +
        '<span style="margin:0 0.25rem;color:var(--text-faint)">·</span>' +
        '<i data-lucide="id-card" class="meta-icon"></i>' +
        '<span>' + escapeHtml(data.userId) + '</span>' +
        '</div>';

      // 대표 설정 버튼
      card.querySelector('.icon-btn--leader').addEventListener('click', function () {
        leaderEnc = enc;
        renderSelectedList();
        updateSubmitBtn();
      });

      // 삭제 버튼
      card.querySelector('.icon-btn--danger').addEventListener('click', function () {
        selectedMap.delete(enc);
        if (leaderEnc === enc) {
          leaderEnc = selectedMap.size > 0 ? selectedMap.keys().next().value : null;
        }

        // 이벤트 재바인딩 없이 클래스만 해제하면 위임 이벤트가 자동 처리
        const searchCard = consultantListEl.querySelector('[data-enc="' + enc + '"]');
        if (searchCard) {
          searchCard.classList.remove('selected');
          searchCard.style.pointerEvents = '';
        }

        renderSelectedList();
        updateSubmitBtn();
      });

      selectedListEl.appendChild(card);
    });

    lucide.createIcons();
  }

  /* ══════════════════════════════════
     페이징 렌더링
  ══════════════════════════════════ */
  function buildPagination(data) {
    if (!data.totalPage || data.totalPage === 0) {
      paginationArea.style.display = 'none';
      return;
    }
    paginationArea.style.display = 'flex';

    const prevCls = data.hasPrev ? '' : 'hidden';
    const nextCls = data.hasNext ? '' : 'hidden';
    let pageNums = '';
    for (let p = data.startPage; p <= data.endPage; p++) {
      pageNums += '<button type="button" class="page-num' + (p === currentPage ? ' active' : '') +
        '" data-page="' + p + '">' + p + '</button>';
    }

    paginationInner.innerHTML =
      '<button type="button" class="page-nav ' + prevCls + '" data-page="' + (data.startPage - 1) + '">' +
      '<i data-lucide="chevron-left"></i>' +
      '</button>' +
      '<div class="page-numbers">' + pageNums + '</div>' +
      '<button type="button" class="page-nav ' + nextCls + '" data-page="' + (data.endPage + 1) + '">' +
      '<i data-lucide="chevron-right"></i>' +
      '</button>';

    paginationInner.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p) && p > 0) doSearch(p);
      });
    });

    lucide.createIcons();
  }

  /* ══════════════════════════════════
     파라미터 수집
  ══════════════════════════════════ */
  function collectParams(page) {
    const params = { page: page || 1, status: 'ACTIVE', hasOrg: 'false' };
    const form = document.getElementById('searchForm');
    ['name', 'nickname'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });
    return params;
  }

  /* ══════════════════════════════════
     AJAX 검색
  ══════════════════════════════════ */
  function doSearch(page) {
    currentPage = page || 1;
    fPage.value = currentPage;
    const params = collectParams(currentPage);

    $.ajax({
      url: '/api/consultant',
      type: 'GET',
      data: params,
      success: function (result) {
        renderSearchResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderSearchResult({ list: [], totalCount: 0, totalPage: 0 });
        } else {
          alert('검색 중 오류가 발생했습니다.');
        }
      }
    });
  }

  /* ══════════════════════════════════
     폼 제출 처리
  ══════════════════════════════════ */
  registerForm.addEventListener('submit', function (e) {
    e.preventDefault();

    if (!nameChecked) {
      nameMsg.textContent = '이름 중복 검사를 완료해주세요.';
      nameMsg.className = 'name-msg error';
      return;
    }
    if (!leaderEnc) {
      alert('대표 컨설턴트를 지정해주세요.');
      return;
    }
    if (selectedMap.size === 0) {
      alert('컨설턴트를 최소 1명 이상 선택해주세요.');
      return;
    }

    // 값 주입
    fOrgNameSubmit.value = orgNameInput.value.trim();
    fEncLeaderNo.value = leaderEnc;

    // encConsultantNos 동적 생성
    encConsultantNosContainer.innerHTML = '';
    selectedMap.forEach(function (data, enc) {
      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'encConsultantNos';
      input.value = enc;
      encConsultantNosContainer.appendChild(input);
    });

    registerForm.submit();
  });

  /* ══════════════════════════════════
     검색 버튼
  ══════════════════════════════════ */
  searchBtn.addEventListener('click', function () {
    doSearch(1);
  });

  /* ══════════════════════════════════
     초기 서버 데이터 렌더링
  ══════════════════════════════════ */
  (function init() {
    if (INIT_RESULT && INIT_RESULT.list && INIT_RESULT.list.length > 0) {
      currentPage = 1;
      totalCountEl.textContent = INIT_RESULT.totalCount || 0;
      consultantListEl.innerHTML = INIT_RESULT.list.map(buildSearchCard).join('');
      lucide.createIcons();
      buildPagination(INIT_RESULT);
    } else {
      consultantListEl.innerHTML =
        '<div class="empty-msg">' +
        '<i data-lucide="search-x" class="empty-icon"></i>' +
        '<p>검색 결과가 없습니다.</p>' +
        '</div>';
      lucide.createIcons();
    }

    // 빈 선택 영역 초기 표시
    selectedListEl.appendChild(selectedEmpty);
  })();

});