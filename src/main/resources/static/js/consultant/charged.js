document.addEventListener('DOMContentLoaded', function () {

  /* ── 서버에서 전달된 선택 사전값 ── */
  const presetConNo = PAGE_DATA.encryptedConsultantNo || null;
  const presetStuNo = PAGE_DATA.encryptedStudentNo || null;

  /* ── 선택 상태 저장 ── */
  let selectedConsultant = presetConNo
    ? { enc: presetConNo, nickname: null, orgName: null, isPreset: true }
    : null;

  // 학생은 배열 (여러 명)
  // presetStuNo가 있으면 미리 넣어두기
  const selectedStudents = presetStuNo
    ? [{ enc: presetStuNo, name: null, isPreset: true }]
    : [];

  /* ════════ 컨설턴트 영역 ════════ */
  const isConPreset = !!presetConNo;

  let cCurrentPage = 1;
  let cInChargedState = null;

  /* ── 라디오 초기화 (컨설턴트 상태 필터) ── */
  function initCStatusRadio() {
    const group = document.getElementById('cStatusGroup');
    if (!group) return;
    const buttons = group.querySelectorAll('.radio-btn');
    const hidden = document.getElementById('cf-status');
    const currentVal = hidden ? hidden.value : '';

    buttons.forEach(function (btn) {
      if (btn.dataset.value === currentVal) btn.classList.add('active');
      btn.addEventListener('click', function () {
        buttons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        if (hidden) hidden.value = btn.dataset.value;
      });
    });

    if (!group.querySelector('.radio-btn.active')) {
      const allBtn = group.querySelector('[data-value=""]');
      if (allBtn) allBtn.classList.add('active');
    }
  }

  /* ── 담당 학생 없음 체크박스 ── */
  function initCNoStudentCheck() {
    const checkbox = document.getElementById('cf-noStudent');
    const startInput = document.getElementById('cf-chargedCountStart');
    const endInput = document.getElementById('cf-chargedCountEnd');
    if (!checkbox) return;

    checkbox.addEventListener('change', function () {
      if (checkbox.checked) {
        cInChargedState = false;
        startInput.disabled = true;
        endInput.disabled = true;
        startInput.value = '';
        endInput.value = '';
      } else {
        startInput.disabled = false;
        endInput.disabled = false;
        const hasRange = startInput.value.trim() !== '' || endInput.value.trim() !== '';
        cInChargedState = hasRange ? true : null;
      }
    });

    [startInput, endInput].forEach(function (input) {
      input.addEventListener('input', function () {
        if (checkbox.checked) return;
        const hasRange = startInput.value.trim() !== '' || endInput.value.trim() !== '';
        cInChargedState = hasRange ? true : null;
      });
    });
  }

  /* ── 컨설턴트 파라미터 수집 ── */
  function collectCParams(page) {
    const params = { page: page || 1 };
    const form = document.getElementById('consultantSearchForm');
    if (!form) return params;

    ['name', 'nickname', 'studentName', 'orgName'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    const statusEl = document.getElementById('cf-status');
    if (statusEl && statusEl.value) params['status'] = statusEl.value;

    const startEl = document.getElementById('cf-chargedCountStart');
    const endEl = document.getElementById('cf-chargedCountEnd');
    if (startEl && !startEl.disabled && startEl.value.trim()) params['chargedCountStart'] = startEl.value.trim();
    if (endEl && !endEl.disabled && endEl.value.trim()) params['chargedCountEnd'] = endEl.value.trim();

    if (cInChargedState !== null) params['inCharged'] = String(cInChargedState);

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

    return '<div class="consultant-card" ' +
      'data-enc="' + escapeHtml(consultant.encryptedConsultantNo) + '" ' +
      'data-nickname="' + escapeHtml(consultant.nickname || '') + '" ' +
      'data-org="' + escapeHtml(consultant.orgName || '-') + '">' +
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

  /* ── 컨설턴트 카드 클릭 처리 ── */
  function bindConsultantCardClick() {
    const listEl = document.getElementById('consultantList');
    if (!listEl) return;
    listEl.addEventListener('click', function (e) {
      const card = e.target.closest('.consultant-card');
      if (!card) return;

      // 이전 선택 해제
      listEl.querySelectorAll('.consultant-card.selected').forEach(c => c.classList.remove('selected'));
      card.classList.add('selected');

      selectedConsultant = {
        enc: card.dataset.enc,
        nickname: card.dataset.nickname,
        orgName: card.dataset.org,
        isPreset: false
      };
      renderAssignConsultant();
    });
  }

  /* ── 컨설턴트 페이징 ── */
  function buildCPagination(data) {
    const area = document.getElementById('cPaginationArea');
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
      const activeClass = p === cCurrentPage ? ' active' : '';
      pageNums += '<button type="button" class="page-num' + activeClass + '" data-page="' + p + '">' + p + '</button>';
    }

    area.innerHTML = '<div class="pagination">' +
      '<button type="button" class="page-nav ' + prevClass + '" data-page="' + (data.startPage - 1) + '">' +
      '<i data-lucide="chevron-left"></i></button>' +
      '<div class="page-numbers">' + pageNums + '</div>' +
      '<button type="button" class="page-nav ' + nextClass + '" data-page="' + (data.endPage + 1) + '">' +
      '<i data-lucide="chevron-right"></i></button>' +
      '</div>';

    area.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p)) doConsultantSearch(p);
      });
    });
    lucide.createIcons();
  }

  /* ── 컨설턴트 AJAX 검색 ── */
  function doConsultantSearch(page) {
    cCurrentPage = page || 1;
    const params = collectCParams(cCurrentPage);

    $.ajax({
      url: '/api/consultant',
      type: 'GET',
      data: params,
      success: function (result) {
        renderConsultantResult(result);
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          renderConsultantResult({ list: [], totalCount: 0, totalPage: 0 });
        } else {
          console.error('컨설턴트 검색 실패', xhr);
          alert('검색 중 오류가 발생했습니다.');
        }
      }
    });
  }

  /* ── 컨설턴트 결과 렌더링 ── */
  function renderConsultantResult(result) {
    const listEl = document.getElementById('consultantList');
    const totalEl = document.getElementById('cTotalCount');
    if (!listEl) return;

    if (totalEl) totalEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      listEl.innerHTML = '<div class="empty-msg"><i data-lucide="search-x" class="empty-icon"></i><p>검색 결과가 없습니다.</p></div>';
      lucide.createIcons();
      buildCPagination({ totalPage: 0 });
      return;
    }

    listEl.innerHTML = result.list.map(buildConsultantCard).join('');

    // 이전에 선택된 카드 복원 표시
    if (selectedConsultant && !selectedConsultant.isPreset) {
      const prev = listEl.querySelector('[data-enc="' + selectedConsultant.enc + '"]');
      if (prev) prev.classList.add('selected');
    }

    lucide.createIcons();
    buildCPagination(result);
  }


  /* ════════ 학생 영역 ════════ */
  const isStuPreset = !!presetStuNo;

  let sCurrentPage = 1;

  /* ── 라디오 초기화 (학생 필터) ── */
  function initStudentRadioGroups() {
    ['sGradeGroup', 'sSemesterGroup', 'sTrackGroup'].forEach(function (groupId) {
      const group = document.getElementById(groupId);
      if (!group) return;
      const buttons = group.querySelectorAll('.radio-btn');
      const inputName = buttons[0]?.dataset.name;
      if (!inputName) return;
      const hidden = document.getElementById('sf-' + inputName);
      const currentVal = hidden ? hidden.value : '';

      buttons.forEach(function (btn) {
        if (btn.dataset.value === currentVal) btn.classList.add('active');
        btn.addEventListener('click', function () {
          buttons.forEach(b => b.classList.remove('active'));
          btn.classList.add('active');
          if (hidden) hidden.value = btn.dataset.value;
        });
      });

      if (!group.querySelector('.radio-btn.active')) {
        const allBtn = group.querySelector('[data-value=""]');
        if (allBtn) allBtn.classList.add('active');
      }
    });
  }

  /* ── 학생 파라미터 수집 ── */
  function collectSParams(page) {
    const params = { page: page || 1, isCharged: 'false' };
    const form = document.getElementById('studentSearchForm');
    if (!form) return params;

    ['name', 'schoolName'].forEach(function (key) {
      const el = form.querySelector('[name="' + key + '"]');
      if (el && el.value.trim()) params[key] = el.value.trim();
    });

    ['grade', 'semester'].forEach(function (key) {
      const el = form.querySelector('#sf-' + key);
      if (el && el.value) params[key] = el.value;
    });

    const trackEl = form.querySelector('#sf-track');
    if (trackEl && trackEl.value) params['track'] = trackEl.value;

    const targetUniv = form.querySelector('[name="targetUniv"]');
    const targetMajor = form.querySelector('[name="targetMajor"]');
    if (targetUniv && targetUniv.value.trim()) params['targetUniv'] = targetUniv.value.trim();
    if (targetMajor && targetMajor.value.trim()) params['targetMajor'] = targetMajor.value.trim();

    return params;
  }

  /* ── 학생 카드 HTML 생성 ── */
  function buildStudentCard(student) {
    const targets = (student.target || []).slice().sort((a, b) => a.ranking - b.ranking);

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
      }).join('') + '</div>'
      : '';

    return '<div class="student-card" ' +
      'data-enc="' + escapeHtml(student.encryptedStudentNo) + '" ' +
      'data-name="' + escapeHtml(student.name || '') + '">' +
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

  /* ── 학생 카드 클릭 처리 ── */
  function bindStudentCardClick() {
    const listEl = document.getElementById('studentList');
    if (!listEl) return;
    listEl.addEventListener('click', function (e) {
      const card = e.target.closest('.student-card');
      if (!card) return;

      card.classList.toggle('selected');

      const enc = card.dataset.enc;
      const name = card.dataset.name;

      if (card.classList.contains('selected')) {
        selectedStudents.push({ enc: enc, name: name, isPreset: false });
      } else {
        // 마지막에 추가된 같은 enc 제거
        const idx = selectedStudents.map(s => s.enc).lastIndexOf(enc);
        if (idx > -1) selectedStudents.splice(idx, 1);
      }
      renderAssignStudents();
    });
  }

  /* ── 학생 페이징 ── */
  function buildSPagination(data) {
    const area = document.getElementById('sPaginationArea');
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
      const activeClass = p === sCurrentPage ? ' active' : '';
      pageNums += '<button type="button" class="page-num' + activeClass + '" data-page="' + p + '">' + p + '</button>';
    }

    area.innerHTML = '<div class="pagination">' +
      '<button type="button" class="page-nav ' + prevClass + '" data-page="' + (data.startPage - 1) + '">' +
      '<i data-lucide="chevron-left"></i></button>' +
      '<div class="page-numbers">' + pageNums + '</div>' +
      '<button type="button" class="page-nav ' + nextClass + '" data-page="' + (data.endPage + 1) + '">' +
      '<i data-lucide="chevron-right"></i></button>' +
      '</div>';

    area.querySelectorAll('.page-num, .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p)) doStudentSearch(p);
      });
    });
    lucide.createIcons();
  }

  /* ── 학생 AJAX 검색 ── */
  function doStudentSearch(page) {
    sCurrentPage = page || 1;
    const params = collectSParams(sCurrentPage);

    $.ajax({
      url: '/api/student',
      type: 'GET',
      data: params,
      success: function (result) {
        renderStudentResult(result);
      },
      error: function (xhr) {
        console.error('학생 검색 실패', xhr);
        alert('검색 중 오류가 발생했습니다.');
      }
    });
  }

  /* ── 학생 결과 렌더링 ── */
  function renderStudentResult(result) {
    const listEl = document.getElementById('studentList');
    const totalEl = document.getElementById('sTotalCount');
    if (!listEl) return;

    if (totalEl) totalEl.textContent = result.totalCount || 0;

    if (!result.list || result.list.length === 0) {
      listEl.innerHTML = '<div class="empty-msg"><i data-lucide="search-x" class="empty-icon"></i><p>검색 결과가 없습니다.</p></div>';
      lucide.createIcons();
      buildSPagination({ totalPage: 0 });
      return;
    }

    listEl.innerHTML = result.list.map(buildStudentCard).join('');

    // 이미 선택된 학생 selected 표시 복원
    const selectedEncs = selectedStudents.filter(s => !s.isPreset).map(s => s.enc);
    listEl.querySelectorAll('.student-card').forEach(function (card) {
      if (selectedEncs.includes(card.dataset.enc)) card.classList.add('selected');
    });

    lucide.createIcons();
    buildSPagination(result);
  }


  /* ════════ 배정 영역 렌더링 ════════ */

  function renderAssignConsultant() {
    const el = document.getElementById('assignConsultant');
    if (!el) return;

    if (!selectedConsultant) {
      el.innerHTML = '<div class="assign-empty" id="assignConsultantEmpty">' +
        '<i data-lucide="user-x" class="assign-empty__icon"></i>' +
        '<span>컨설턴트를 선택해 주세요</span>' +
        '</div>';
      lucide.createIcons();
      return;
    }

    if (selectedConsultant.isPreset) {
      // 이미 preset이면 HTML에서 렌더링됨 — 변경 불필요
      return;
    }

    const nickname = escapeHtml(selectedConsultant.nickname || '');
    const orgName = escapeHtml(selectedConsultant.orgName || '-');

    el.innerHTML = '<div class="assign-tag">' +
      '<i data-lucide="contact" class="assign-tag__icon"></i>' +
      '<span>' + nickname + '</span>' +
      (orgName ? '<span style="color:var(--text-faint);font-size:0.8125rem;">ㅣ' + orgName + '</span>' : '') +
      '</div>';
    lucide.createIcons();
  }

  function renderAssignStudents() {
    const el = document.getElementById('assignStudents');
    if (!el) return;

    const dynamicStudents = selectedStudents.filter(s => !s.isPreset);

    if (isStuPreset) {
      // preset 태그는 그대로 두고, dynamic 태그는 preset 뒤에 추가
      // preset 태그를 제외한 동적 태그만 갱신
      let dynamicHtml = '';
      dynamicStudents.forEach(function (s) {
        dynamicHtml += '<div class="assign-tag" data-dynamic-enc="' + escapeHtml(s.enc) + '">' +
          '<i data-lucide="graduation-cap" class="assign-tag__icon"></i>' +
          '<span>' + escapeHtml(s.name || '') + '</span>' +
          '</div>';
      });

      // 기존 dynamic 태그 제거 후 재삽입
      el.querySelectorAll('[data-dynamic-enc]').forEach(t => t.remove());
      el.insertAdjacentHTML('beforeend', dynamicHtml);

    } else {
      if (dynamicStudents.length === 0) {
        el.innerHTML = '<div class="assign-empty" id="assignStudentsEmpty">' +
          '<i data-lucide="users-x" class="assign-empty__icon"></i>' +
          '<span>학생을 선택해 주세요</span>' +
          '</div>';
      } else {
        el.innerHTML = dynamicStudents.map(function (s) {
          return '<div class="assign-tag">' +
            '<i data-lucide="graduation-cap" class="assign-tag__icon"></i>' +
            '<span>' + escapeHtml(s.name || '') + '</span>' +
            '</div>';
        }).join('');
      }
    }

    lucide.createIcons();
  }


  /* ════════ 배정하기 버튼 ════════ */
  document.getElementById('assignBtn').addEventListener('click', function () {

    const conEnc = selectedConsultant ? selectedConsultant.enc : null;
    if (!conEnc) {
      alert('컨설턴트를 선택해 주세요.');
      return;
    }
    if (selectedStudents.length === 0) {
      alert('학생을 선택해 주세요.');
      return;
    }

    const params = {
      encryptedConsultantNo: conEnc
    };

    // encryptedStudentNos 배열을 반복 파라미터로 전송
    const stuEncs = selectedStudents.map(s => s.enc);

    $.ajax({
      url: '/api/consultant/charged',
      type: 'POST',
      data: buildFormData(params, stuEncs),
      success: function () {
        if (presetStuNo) {
          location.href = '/student/' + presetStuNo;
        } else {
          location.reload();
        }
      },
      error: function (xhr) {
        console.error('배정 실패', xhr);
        alert('배정 중 오류가 발생했습니다.');
      }
    });
  });

  /* encryptedStudentNos를 List<String>으로 인식시키기 위한 FormData 직렬화 */
  function buildFormData(params, stuEncs) {
    const parts = [];
    Object.keys(params).forEach(function (key) {
      parts.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
    });
    stuEncs.forEach(function (enc) {
      parts.push(encodeURIComponent('encryptedStudentNos') + '=' + encodeURIComponent(enc));
    });
    return parts.join('&');
  }

  /* ── HTML 이스케이프 ── */
  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }


  /* ════════ 이벤트 바인딩 ════════ */

  if (!isConPreset) {
    const cSearchBtn = document.getElementById('consultantSearchBtn');
    if (cSearchBtn) cSearchBtn.addEventListener('click', function () { doConsultantSearch(1); });

    document.querySelectorAll('#cPaginationArea .page-num, #cPaginationArea .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p)) doConsultantSearch(p);
      });
    });

    bindConsultantCardClick();
    initCStatusRadio();
    initCNoStudentCheck();
  }

  if (!isStuPreset) {
    const sSearchBtn = document.getElementById('studentSearchBtn');
    if (sSearchBtn) sSearchBtn.addEventListener('click', function () { doStudentSearch(1); });

    document.querySelectorAll('#sPaginationArea .page-num, #sPaginationArea .page-nav').forEach(function (btn) {
      btn.addEventListener('click', function () {
        const p = parseInt(btn.dataset.page, 10);
        if (!isNaN(p)) doStudentSearch(p);
      });
    });

    bindStudentCardClick();
    initStudentRadioGroups();
  }

});