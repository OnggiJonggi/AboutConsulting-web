document.addEventListener('DOMContentLoaded', function () {
  const state = {
    selectedOrg: null,
    selectedConsultants: {},
    leaderEnc: null,
    orgLocked: !!PAGE_DATA.encOrgNo,
    consultantLocked: !!PAGE_DATA.encConNo
  };

  init();

  function init() {
    initializeAssignmentFromServer();
    bindOrgSearch();
    bindConsultantSearch();
    bindAssignAreaEvents();
    bindAssignSubmit();
    syncResultSelectionStates();
    lucide.createIcons();
  }

  function initializeAssignmentFromServer() {
    if (PAGE_DATA.encConNo) {
      addConsultant(buildPresetConsultant(), true, true);
      state.leaderEnc = PAGE_DATA.encConNo;
    }

    if (PAGE_DATA.orgBasicInfo) {
      applyOrgDetail(PAGE_DATA.orgBasicInfo, PAGE_DATA.encOrgNo || PAGE_DATA.orgBasicInfo.encOrgNo);
      return;
    }

    ensureLeader();
    renderAssignedArea();
  }

  function buildPresetConsultant() {
    return {
      encConsultantNo: PAGE_DATA.encConNo,
      nickname: '선택된 컨설턴트',
      userId: '',
      name: '',
      studentCount: 0,
      preset: true
    };
  }

  function normalizeConsultant(consultant) {
    return {
      encConsultantNo: consultant.encConsultantNo || consultant.encConsultantNo,
      nickname: consultant.nickname || '선택된 컨설턴트',
      userId: consultant.userId || '',
      name: consultant.name || '',
      studentCount: Number(consultant.studentCount || 0),
      preset: !!consultant.preset
    };
  }

  function getConsultantsArray() {
    return Object.values(state.selectedConsultants);
  }

  function getPresetConsultant() {
    const preset = getConsultantsArray().find(function (item) {
      return item.preset;
    });
    if (preset) return preset;
    if (PAGE_DATA.encConNo) return buildPresetConsultant();
    return null;
  }

  function addConsultant(consultant, silent, deferRender) {
    if (!consultant || !consultant.encConsultantNo) return false;

    const normalized = normalizeConsultant(consultant);
    const enc = normalized.encConsultantNo;

    if (state.selectedConsultants[enc]) {
      if (!silent) alert('이미 선택된 컨설턴트입니다.');
      return false;
    }

    state.selectedConsultants[enc] = normalized;

    if (!state.leaderEnc) {
      state.leaderEnc = enc;
    }

    if (!deferRender) {
      ensureLeader();
      renderAssignedArea();
      syncResultSelectionStates();
    }

    return true;
  }

  function removeConsultant(enc) {
    const target = state.selectedConsultants[enc];
    if (!target) return;
    if (target.preset) return;

    delete state.selectedConsultants[enc];

    if (state.leaderEnc === enc) {
      state.leaderEnc = null;
    }

    ensureLeader();
    renderAssignedArea();
    syncResultSelectionStates();
  }

  function ensureLeader() {
    const keys = Object.keys(state.selectedConsultants);

    if (keys.length === 0) {
      state.leaderEnc = null;
      return;
    }

    if (!state.leaderEnc || !state.selectedConsultants[state.leaderEnc]) {
      state.leaderEnc = keys[0];
    }
  }

  function clearOrgSelection() {
    if (state.orgLocked) return;

    state.selectedOrg = null;
    const preset = getPresetConsultant();

    state.selectedConsultants = {};

    if (preset) {
      state.selectedConsultants[preset.encConsultantNo] = normalizeConsultant(preset);
      state.leaderEnc = preset.encConsultantNo;
    } else {
      state.leaderEnc = null;
    }

    ensureLeader();
    renderAssignedArea();
    syncResultSelectionStates();
  }

  function applyOrgDetail(detail, encOrgNo) {
    state.selectedOrg = {
      encOrgNo: encOrgNo || detail.encOrgNo || '',
      name: detail.name || '',
      consultantCount: Number(detail.consultantCount || 0),
      studentCount: Number(detail.studentCount || 0),
      leaderNickname: detail.leaderNickname || ''
    };

    const preset = getPresetConsultant();
    state.selectedConsultants = {};

    if (preset) {
      state.selectedConsultants[preset.encConsultantNo] = normalizeConsultant(preset);
    }

    const consultantList = Array.isArray(detail.consultantDetail) ? detail.consultantDetail : [];
    consultantList.forEach(function (item) {
      addConsultant({
        encConsultantNo: item.encConsultantNo,
        nickname: item.nickname,
        userId: item.userId,
        name: item.name,
        studentCount: item.studentCount,
        preset: false
      }, true, true);
    });

    state.leaderEnc = null;

    const byNickname = getConsultantsArray().find(function (item) {
      return state.selectedOrg.leaderNickname && item.nickname === state.selectedOrg.leaderNickname;
    });

    if (byNickname) {
      state.leaderEnc = byNickname.encConsultantNo;
    }

    ensureLeader();
    renderAssignedArea();
    syncResultSelectionStates();
  }

  function bindOrgSearch() {
    const searchBtn = document.getElementById('orgSearchBtn');
    const listEl = document.getElementById('orgList');
    const pagingArea = document.getElementById('orgPaginationArea');

    if (searchBtn) {
      searchBtn.addEventListener('click', function () {
        doOrgSearch(1);
      });
    }

    if (listEl) {
      listEl.addEventListener('click', function (e) {
        const card = e.target.closest('.org-card');
        if (!card) return;

        const encOrgNo = card.getAttribute('data-enc');
        if (!encOrgNo) return;

        fetchOrgDetail(encOrgNo);
      });
    }

    if (pagingArea) {
      pagingArea.addEventListener('click', function (e) {
        const btn = e.target.closest('.page-num, .page-nav');
        if (!btn) return;

        const page = parseInt(btn.getAttribute('data-page'), 10);
        if (!isNaN(page)) doOrgSearch(page);
      });
    }
  }

  function bindConsultantSearch() {
    const searchBtn = document.getElementById('consultantSearchBtn');
    const listEl = document.getElementById('consultantList');
    const pagingArea = document.getElementById('consultantPaginationArea');

    if (searchBtn) {
      searchBtn.addEventListener('click', function () {
        doConsultantSearch(1);
      });
    }

    if (listEl) {
      listEl.addEventListener('click', function (e) {
        const card = e.target.closest('.consultant-card');
        if (!card) return;

        addConsultant({
          encConsultantNo: card.getAttribute('data-enc'),
          nickname: card.getAttribute('data-nickname'),
          userId: card.getAttribute('data-userid'),
          name: card.getAttribute('data-name'),
          studentCount: card.getAttribute('data-student-count'),
          preset: false
        }, false, false);
      });
    }

    if (pagingArea) {
      pagingArea.addEventListener('click', function (e) {
        const btn = e.target.closest('.page-num, .page-nav');
        if (!btn) return;

        const page = parseInt(btn.getAttribute('data-page'), 10);
        if (!isNaN(page)) doConsultantSearch(page);
      });
    }
  }

  function collectOrgParams(page) {
    const form = document.getElementById('orgSearchForm');
    const params = {
      page: page || 1,
      status: 'ACTIVE'
    };

    if (!form) return params;

    const nameEl = form.querySelector('[name="name"]');
    if (nameEl && nameEl.value.trim()) {
      params.name = nameEl.value.trim();
    }

    return params;
  }

  function collectConsultantParams(page) {
    const form = document.getElementById('consultantSearchForm');
    const params = {
      page: page || 1,
      status: 'ACTIVE',
      hasOrg: 'false'
    };

    if (!form) return params;

    ['name', 'nickname'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) {
        params[key] = el.value.trim();
      }
    });

    return params;
  }

  function doOrgSearch(page) {
    $.ajax({
      url: '/api/org',
      type: 'GET',
      data: collectOrgParams(page),
      success: function (result) {
        renderOrgResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderOrgResult({ list: [], totalCount: 0, totalPage: 0 });
          return;
        }
        console.error(xhr);
        alert('소속 검색 중 오류가 발생했습니다.');
      }
    });
  }

  function doConsultantSearch(page) {
    $.ajax({
      url: '/api/consultant',
      type: 'GET',
      data: collectConsultantParams(page),
      success: function (result) {
        renderConsultantResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderConsultantResult({ list: [], totalCount: 0, totalPage: 0 });
          return;
        }
        console.error(xhr);
        alert('컨설턴트 검색 중 오류가 발생했습니다.');
      }
    });
  }

  function fetchOrgDetail(encOrgNo) {
    $.ajax({
      url: '/api/org/' + encodeURIComponent(encOrgNo),
      type: 'GET',
      success: function (result) {
        applyOrgDetail(result, encOrgNo);
      },
      error: function (xhr) {
        console.error(xhr);
        alert('소속 상세 조회 중 오류가 발생했습니다.');
      }
    });
  }

  function renderOrgResult(result) {
    const listEl = document.getElementById('orgList');
    const countEl = document.getElementById('orgTotalCount');
    const pagingEl = document.getElementById('orgPaginationArea');

    if (!listEl) return;
    if (countEl) countEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      listEl.innerHTML =
        '<div class="empty-msg">' +
        '<i data-lucide="search-x" class="empty-icon"></i>' +
        '<p>검색 결과가 없습니다.</p>' +
        '</div>';
      buildPagination(pagingEl, { totalPage: 0 }, doOrgSearch);
      lucide.createIcons();
      return;
    }

    listEl.innerHTML = result.list.map(buildOrgCardHtml).join('');
    buildPagination(pagingEl, result, doOrgSearch);
    syncResultSelectionStates();
    lucide.createIcons();
  }

  function renderConsultantResult(result) {
    const listEl = document.getElementById('consultantList');
    const countEl = document.getElementById('consultantTotalCount');
    const pagingEl = document.getElementById('consultantPaginationArea');

    if (!listEl) return;
    if (countEl) countEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      listEl.innerHTML =
        '<div class="empty-msg">' +
        '<i data-lucide="search-x" class="empty-icon"></i>' +
        '<p>검색 결과가 없습니다.</p>' +
        '</div>';
      buildPagination(pagingEl, { totalPage: 0 }, doConsultantSearch);
      lucide.createIcons();
      return;
    }

    listEl.innerHTML = result.list.map(buildConsultantCardHtml).join('');
    buildPagination(pagingEl, result, doConsultantSearch);
    syncResultSelectionStates();
    lucide.createIcons();
  }

  function buildOrgCardHtml(org) {
    return '' +
      '<div class="org-card" ' +
      'data-enc="' + escapeHtml(org.encOrgNo || '') + '" ' +
      'data-name="' + escapeHtml(org.name || '') + '" ' +
      'data-consultant-count="' + escapeHtml(String(org.consultantCount || 0)) + '" ' +
      'data-student-count="' + escapeHtml(String(org.studentCount || 0)) + '" ' +
      'data-leader-nickname="' + escapeHtml(org.leaderNickname || '') + '">' +
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
      '<div class="card-row card-row--secondary">' +
      '<span class="card-stat" title="소속 컨설턴트 수">' +
      '<i data-lucide="users" class="meta-icon"></i>' +
      '<span>' + escapeHtml(String(org.consultantCount || 0)) + '명</span>' +
      '</span>' +
      '<span class="card-stat" title="전체 담당 학생 수">' +
      '<i data-lucide="graduation-cap" class="meta-icon"></i>' +
      '<span>' + escapeHtml(String(org.studentCount || 0)) + '명</span>' +
      '</span>' +
      '</div>' +
      '</div>';
  }

  function buildConsultantCardHtml(consultant) {
    return '' +
      '<div class="consultant-card" ' +
      'data-enc="' + escapeHtml(consultant.encConsultantNo || '') + '" ' +
      'data-nickname="' + escapeHtml(consultant.nickname || '') + '" ' +
      'data-userid="' + escapeHtml(consultant.userId || '') + '" ' +
      'data-name="' + escapeHtml(consultant.name || '') + '" ' +
      'data-student-count="' + escapeHtml(String(consultant.studentCount || 0)) + '">' +
      '<div class="card-row card-row--primary">' +
      '<span class="card-nickname">' +
      '<i data-lucide="contact" class="card-icon"></i>' +
      '<span>' + escapeHtml(consultant.nickname || '') + '</span>' +
      '</span>' +
      '<span class="card-student-count">' +
      '<i data-lucide="users-round" class="card-icon-sm"></i>' +
      '<span>' + escapeHtml(String(consultant.studentCount || 0)) + '명</span>' +
      '</span>' +
      '</div>' +
      '<div class="card-row card-row--secondary">' +
      '<span class="card-userid">' +
      '<i data-lucide="id-card" class="meta-icon"></i>' +
      '<span>' + escapeHtml(consultant.userId || '') + '</span>' +
      '</span>' +
      '<span class="card-name">' +
      '<i data-lucide="user-round" class="meta-icon"></i>' +
      '<span>' + escapeHtml(consultant.name || '') + '</span>' +
      '</span>' +
      '</div>' +
      '</div>';
  }

  function buildPagination(container, data, callback) {
    if (!container) return;

    if (!data.totalPage || data.totalPage === 0) {
      container.style.display = 'none';
      container.innerHTML = '';
      return;
    }

    container.style.display = 'flex';

    let pageNumbers = '';
    for (let p = data.startPage; p <= data.endPage; p++) {
      pageNumbers += '<button type="button" class="page-num' +
        ((data.page && data.page === p) ? ' active' : '') +
        '" data-page="' + p + '">' + p + '</button>';
    }

    container.innerHTML =
      '<div class="pagination">' +
      '<button type="button" class="page-nav ' + (data.hasPrev ? '' : 'hidden') + '" data-page="' + (data.startPage - 1) + '">' +
      '<i data-lucide="chevron-left"></i>' +
      '</button>' +
      '<div class="page-numbers">' + pageNumbers + '</div>' +
      '<button type="button" class="page-nav ' + (data.hasNext ? '' : 'hidden') + '" data-page="' + (data.endPage + 1) + '">' +
      '<i data-lucide="chevron-right"></i>' +
      '</button>' +
      '</div>';

    container.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const page = parseInt(btn.getAttribute('data-page'), 10);
        if (!isNaN(page)) callback(page);
      });
    });
  }

  function bindAssignAreaEvents() {
    const orgSlot = document.getElementById('assignedOrgSlot');
    const consultantSlot = document.getElementById('assignedConsultantSlot');

    if (orgSlot) {
      orgSlot.addEventListener('click', function (e) {
        const removeBtn = e.target.closest('[data-action="remove-org"]');
        if (!removeBtn || removeBtn.disabled) return;
        clearOrgSelection();
      });
    }

    if (consultantSlot) {
      consultantSlot.addEventListener('click', function (e) {
        const removeBtn = e.target.closest('[data-action="remove-consultant"]');
        if (removeBtn) {
          if (removeBtn.disabled) return;
          removeConsultant(removeBtn.getAttribute('data-enc'));
          return;
        }

        const leaderBtn = e.target.closest('[data-action="set-leader"]');
        if (leaderBtn) {
          const enc = leaderBtn.getAttribute('data-enc');
          if (!enc || !state.selectedConsultants[enc]) return;
          state.leaderEnc = enc;
          renderAssignedArea();
          syncResultSelectionStates();
        }
      });
    }
  }

  function renderAssignedArea() {
    renderAssignedOrg();
    renderAssignedConsultants();
    updateAssignButtonState();
    lucide.createIcons();
  }

  function renderAssignedOrg() {
    const slot = document.getElementById('assignedOrgSlot');
    if (!slot) return;

    if (!state.selectedOrg) {
      slot.innerHTML =
        '<div class="assign-empty">' +
        '<i data-lucide="building-2" class="assign-empty__icon"></i>' +
        '<span>선택된 소속이 없습니다.</span>' +
        '</div>';
      return;
    }

    slot.innerHTML =
      '<div class="assigned-org-card">' +
      '<div class="assigned-card__actions">' +
      '<button type="button" class="emoji-btn" data-action="remove-org" title="삭제하기" ' + (state.orgLocked ? 'disabled' : '') + '>❌</button>' +
      '</div>' +
      '<div class="assigned-card__title">' +
      '<i data-lucide="building-2" class="card-icon"></i>' +
      '<span>' + escapeHtml(state.selectedOrg.name || '') + '</span>' +
      '</div>' +
      '<div class="assigned-card__meta">' +
      '<span class="meta-chip"><i data-lucide="users" class="meta-icon"></i>컨설턴트 ' + escapeHtml(String(state.selectedOrg.consultantCount || 0)) + '명</span>' +
      '<span class="meta-chip"><i data-lucide="graduation-cap" class="meta-icon"></i>학생 ' + escapeHtml(String(state.selectedOrg.studentCount || 0)) + '명</span>' +
      '</div>' +
      (state.orgLocked ? '<span class="preset-badge">고정 선택</span>' : '') +
      '</div>';
  }

  function renderAssignedConsultants() {
    const slot = document.getElementById('assignedConsultantSlot');
    if (!slot) return;

    const consultants = getConsultantsArray();

    if (consultants.length === 0) {
      slot.innerHTML =
        '<div class="assign-empty">' +
        '<i data-lucide="users" class="assign-empty__icon"></i>' +
        '<span>선택된 컨설턴트가 없습니다.</span>' +
        '</div>';
      return;
    }

    slot.innerHTML = consultants.map(function (consultant) {
      const isLeader = state.leaderEnc === consultant.encConsultantNo;
      const removeDisabled = consultant.preset ? 'disabled' : '';

      return '' +
        '<div class="assigned-consultant-card">' +
        '<div class="assigned-card__actions">' +
        '<button type="button" class="emoji-btn ' + (isLeader ? 'is-active' : '') + '" data-action="set-leader" data-enc="' + escapeHtml(consultant.encConsultantNo) + '" title="대표 컨설턴트로 지정하기">👑</button>' +
        '<button type="button" class="emoji-btn" data-action="remove-consultant" data-enc="' + escapeHtml(consultant.encConsultantNo) + '" title="삭제하기" ' + removeDisabled + '>❌</button>' +
        '</div>' +
        '<div class="assigned-card__title">' +
        '<i data-lucide="contact" class="card-icon"></i>' +
        '<span>' + escapeHtml(consultant.nickname || '선택된 컨설턴트') + '</span>' +
        '</div>' +
        '<div class="assigned-card__meta">' +
        (consultant.userId ? '<span class="meta-chip"><i data-lucide="id-card" class="meta-icon"></i>' + escapeHtml(consultant.userId) + '</span>' : '') +
        (consultant.name ? '<span class="meta-chip"><i data-lucide="user-round" class="meta-icon"></i>' + escapeHtml(consultant.name) + '</span>' : '') +
        '<span class="meta-chip"><i data-lucide="users-round" class="meta-icon"></i>학생 ' + escapeHtml(String(consultant.studentCount || 0)) + '명</span>' +
        '</div>' +
        (isLeader ? '<span class="leader-badge">대표 컨설턴트</span>' : '') +
        (consultant.preset ? '<span class="preset-badge">고정 선택</span>' : '') +
        '</div>';
    }).join('');
  }

  function updateAssignButtonState() {
    const btn = document.getElementById('assignBtn');
    if (!btn) return;

    const hasOrg = !!(state.selectedOrg && state.selectedOrg.encOrgNo);
    const hasConsultant = getConsultantsArray().length > 0;
    const hasLeader = !!state.leaderEnc;

    btn.disabled = !(hasOrg && hasConsultant && hasLeader);
  }

  function bindAssignSubmit() {
    const btn = document.getElementById('assignBtn');
    if (!btn) return;

    btn.addEventListener('click', function () {
      if (!state.selectedOrg || !state.selectedOrg.encOrgNo) {
        alert('소속을 먼저 선택해 주세요.');
        return;
      }

      const consultants = getConsultantsArray();
      if (consultants.length === 0) {
        alert('배정할 컨설턴트를 선택해 주세요.');
        return;
      }

      if (!state.leaderEnc) {
        alert('대표 컨설턴트를 지정해 주세요.');
        return;
      }

      const encConNos = consultants.map(function (item) {
        return item.encConsultantNo;
      });

      $.ajax({
        url: '/api/org/charged',
        type: 'POST',
        traditional: true,
        data: {
          encOrgNo: state.selectedOrg.encOrgNo,
          encLederNo: state.leaderEnc,
          encConNos: encConNos
        },
        success: function () {
          if (PAGE_DATA.encConNo) {
            history.back();
          } else {
            location.reload();
          }
        },
        error: function (xhr) {
          console.error(xhr);
          alert('배정 처리 중 오류가 발생했습니다.');
        }
      });
    });
  }

  function syncResultSelectionStates() {
    document.querySelectorAll('#orgList .org-card').forEach(function (card) {
      const enc = card.getAttribute('data-enc');
      card.classList.toggle('is-selected-in-result', !!state.selectedOrg && state.selectedOrg.encOrgNo === enc);
    });

    document.querySelectorAll('#consultantList .consultant-card').forEach(function (card) {
      const enc = card.getAttribute('data-enc');
      card.classList.toggle('is-selected-in-result', !!state.selectedConsultants[enc]);
    });
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }
});