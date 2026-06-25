(function () {

  /* ── 네비 클릭 이벤트 ── */
  document.querySelectorAll('.detail-nav__item').forEach(function (navItem) {
    navItem.addEventListener('click', function (e) {
      e.preventDefault();

      const href = this.getAttribute('href');
      if (!href) return;

      // '기본 정보'로 돌아올 때는 새로고침
      const isBasicTab = this.id === 'nav-basic';
      if (isBasicTab) {
        location.href = href;
        return;
      }

      // 이미 active인 탭은 무시
      if (this.classList.contains('active')) return;

      // Ajax 조각 로드
      loadFragment(href, this);
    });
  });

  function loadFragment(url, clickedNav) {
    $.ajax({
      url: url,
      method: 'GET',
      success: function (html) {
        // 네비 active 전환
        document.querySelectorAll('.detail-nav__item').forEach(el => el.classList.remove('active'));
        clickedNav.classList.add('active');

        // 콘텐츠 교체
        document.getElementById('detail-content').innerHTML = html;
        lucide.createIcons();

        // record 초기화
        if (typeof window.initRecord === 'function') window.initRecord();
        if (typeof window.initMock === 'function') window.initMock();
        if (typeof window.initOverview === 'function') window.initOverview();
      },
      error: function () {
        alert('페이지를 불러오는 중 오류가 발생했습니다.');
      }
    });
  }

})();

/* ══════════════════════════════════════════════
   main-edit.js — 정보 수정 모달 + 상태 수정 모달
══════════════════════════════════════════════ */
(function () {

  /* ── 공통 상수 ── */
  const REGEXP = {
    NAME: /^[ㄱ-ㅎ가-힣]{1,10}$/,
    TARGET_MAJOR: /^[A-Za-z0-9ㄱ-ㅎ가-힣\s/()]{1,20}$/,
    TARGET_UNIV: /^[A-Za-z0-9ㄱ-ㅎ가-힣\s·.,/()]{1,20}$/
  };

  /* ════════════════════════════════════
     유효성 헬퍼
  ════════════════════════════════════ */
  function showErr(id, msg) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = msg;
    el.classList.add('show');
    const prev = el.previousElementSibling;
    if (prev && prev.classList.contains('form-control')) prev.classList.add('is-invalid');
  }

  function hideErr(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = '';
    el.classList.remove('show');
    const prev = el.previousElementSibling;
    if (prev && prev.classList.contains('form-control')) prev.classList.remove('is-invalid');
  }

  function showTargetFieldError(el, msg) {
    el.classList.add('is-invalid');
    const errEl = el.closest('.target-input-wrap').querySelector('.invalid-feedback');
    if (errEl) { errEl.textContent = msg; errEl.classList.add('show'); }
  }

  function clearTargetFieldError(el) {
    el.classList.remove('is-invalid');
    const errEl = el.closest('.target-input-wrap').querySelector('.invalid-feedback');
    if (errEl) { errEl.textContent = ''; errEl.classList.remove('show'); }
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;')
      .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  /* ════════════════════════════════════
     학교 검색 패널 (수정 모달 전용)
  ════════════════════════════════════ */
  window.basicEditOpenSchoolPanel = function () {
    document.getElementById('basic-edit-schoolPanel').style.display = 'block';
    document.getElementById('basic-edit-schoolNameInput').focus();
  };

  window.basicEditCloseSchoolPanel = function () {
    const panel = document.getElementById('basic-edit-schoolPanel');
    const area = document.getElementById('basic-edit-schoolResultArea');
    panel.style.display = 'none';
    area.style.display = 'none';
    area.innerHTML = '';
  };

  function renderEditSchoolResults(data, mode) {
    const area = document.getElementById('basic-edit-schoolResultArea');
    area.style.display = 'block';
    area.innerHTML = '';

    if (!data || data.length === 0) {
      area.innerHTML = '<div class="school-empty-msg"><p>검색된 학교가 없습니다. 학교를 추가해 주세요.</p></div>';
      return;
    }

    const table = document.createElement('table');
    table.className = 'school-table';
    table.innerHTML = `
      <thead><tr>
        <th>학교명</th><th>시/도</th><th>시/군/구</th>
        <th>공학여부</th><th>특목고</th><th>특목고 종류</th>
      </tr></thead>
      <tbody id="basic-edit-schoolTableBody"></tbody>`;

    const tbody = table.querySelector('#basic-edit-schoolTableBody');
    data.forEach(function (school) {
      const tr = document.createElement('tr');
      tr.className = 'school-table-row';
      tr.innerHTML = `
        <td>${escapeHtml(school.name)}</td>
        <td>${escapeHtml(school.sido)}</td>
        <td>${escapeHtml(school.sigungu || '-')}</td>
        <td>${escapeHtml(school.coeducation)}</td>
        <td>${escapeHtml(school.specialized || '일반고')}</td>
        <td>${escapeHtml(school.specializedType || '-')}</td>`;
      tr.addEventListener('click', function () {
        if (mode === 'search') {
          selectEditSchool(school.schoolCode, school.name);
        } else {
          confirmAddEditSchool(school.schoolCode, school.name);
        }
      });
      tbody.appendChild(tr);
    });
    area.appendChild(table);
  }

  function searchEditSchool() {
    const name = document.getElementById('basic-edit-schoolNameInput').value.trim();
    if (!name) { showEditSchoolMsg('학교 이름을 입력하세요.'); return; }
    $.ajax({
      url: '/api/school', method: 'GET', data: { schoolName: name },
      success: function (data) { renderEditSchoolResults(data, 'search'); },
      error: function () { showEditSchoolMsg('검색 중 오류가 발생했습니다.'); }
    });
  }

  function openAddEditSchool() {
    const name = document.getElementById('basic-edit-schoolNameInput').value.trim();
    if (!name) { showEditSchoolMsg('학교 이름을 입력하세요.'); return; }
    $.ajax({
      url: '/api/school/high', method: 'GET', data: { schoolName: name },
      success: function (data) { renderEditSchoolResults(data, 'add'); },
      error: function () { showEditSchoolMsg('요청 중 오류가 발생했습니다.'); }
    });
  }

  function selectEditSchool(code, name) {
    document.getElementById('basic-edit-schoolCode').value = code;
    const text = document.getElementById('basic-edit-schoolDisplayText');
    text.textContent = name;
    text.className = 'school-display-name';
    document.getElementById('basic-edit-schoolDisplay').classList.add('has-value');
    hideErr('basic-edit-schoolError');
    basicEditCloseSchoolPanel();
    lucide.createIcons();
  }

  function confirmAddEditSchool(code, name) {
    $.ajax({
      url: '/api/school/register', method: 'POST', data: { schoolCode: code },
      success: function () { selectEditSchool(code, name); },
      statusCode: {
        409: function () { showEditSchoolMsg('이미 등록된 학교입니다. 다시 검색해 주세요.'); }
      },
      error: function (xhr) {
        if (xhr.status !== 409) showEditSchoolMsg('오류가 발생했습니다. 다시 시도해 주세요.');
      }
    });
  }

  function showEditSchoolMsg(msg) {
    const area = document.getElementById('basic-edit-schoolResultArea');
    area.style.display = 'block';
    area.innerHTML = `<div class="school-empty-msg"><p>${escapeHtml(msg)}</p></div>`;
  }

  /* ════════════════════════════════════
     수정 폼 유효성 검사
  ════════════════════════════════════ */
  function validateEditForm() {
    let valid = true;

    // 이름
    const name = document.getElementById('basic-edit-name').value.trim();
    hideErr('basic-edit-nameError');
    if (!name) {
      showErr('basic-edit-nameError', '이름을 입력해 주세요.');
      document.getElementById('basic-edit-name').classList.add('is-invalid');
      valid = false;
    } else if (!REGEXP.NAME.test(name)) {
      showErr('basic-edit-nameError', '이름은 한글 1~10자로 입력해 주세요.');
      document.getElementById('basic-edit-name').classList.add('is-invalid');
      valid = false;
    } else {
      document.getElementById('basic-edit-name').classList.remove('is-invalid');
    }

    // 학교
    hideErr('basic-edit-schoolError');
    if (!document.getElementById('basic-edit-schoolCode').value) {
      showErr('basic-edit-schoolError', '학교를 선택해 주세요.');
      valid = false;
    }

    // 학년
    hideErr('basic-edit-gradeError');
    if (!document.querySelector('input[name="basic-edit-grade"]:checked')) {
      showErr('basic-edit-gradeError', '학년을 선택해 주세요.');
      valid = false;
    }

    // 학기
    hideErr('basic-edit-semesterError');
    if (!document.querySelector('input[name="basic-edit-semester"]:checked')) {
      showErr('basic-edit-semesterError', '학기를 선택해 주세요.');
      valid = false;
    }

    // 계열
    hideErr('basic-edit-trackError');
    if (!document.querySelector('input[name="basic-edit-track"]:checked')) {
      showErr('basic-edit-trackError', '계열을 선택해 주세요.');
      valid = false;
    }

    // 목표 대학/전공
    hideErr('basic-edit-targetPairError');
    const items = document.querySelectorAll('#basic-edit-targetList .target-item');
    let targetPairError = false;
    let completePairCount = 0;

    items.forEach(function (item) {
      const index = item.getAttribute('data-index');
      const univInput = item.querySelector('.target-univ');
      const majInput = item.querySelector('.target-major');
      const rankInput = item.querySelector(`input[name="target[${index}].ranking"]`);
      if (rankInput) rankInput.value = parseInt(index, 10) + 1;

      const univ = univInput.value.trim();
      const maj = majInput.value.trim();
      clearTargetFieldError(univInput);
      clearTargetFieldError(majInput);

      if (!univ && !maj) return;

      if (univ && !maj) {
        showTargetFieldError(majInput, '목표 전공도 함께 입력해 주세요.');
        targetPairError = true; valid = false; return;
      }
      if (!univ && maj) {
        showTargetFieldError(univInput, '목표 대학도 함께 입력해 주세요.');
        targetPairError = true; valid = false; return;
      }

      let rowValid = true;
      if (!REGEXP.TARGET_UNIV.test(univ)) {
        showTargetFieldError(univInput, '대학 이름은 한글/영문/숫자 1~20자로 입력해 주세요.');
        valid = false; rowValid = false;
      }
      if (!REGEXP.TARGET_MAJOR.test(maj)) {
        showTargetFieldError(majInput, '전공은 한글/영문/숫자 1~20자로 입력해 주세요.');
        valid = false; rowValid = false;
      }
      if (rowValid) completePairCount++;
    });

    if (completePairCount === 0) {
      showErr('basic-edit-targetPairError', '목표 대학/전공은 최소 1쌍 이상 입력해 주세요.');
      valid = false;
    }

    return valid;
  }

  /* ════════════════════════════════════
     정보 수정 모달 열기/닫기
  ════════════════════════════════════ */
  function openEditModal() {
    document.getElementById('basic-edit-modal').style.display = 'flex';
    lucide.createIcons();
  }

  function closeEditModal() {
    document.getElementById('basic-edit-modal').style.display = 'none';
    basicEditCloseSchoolPanel();
  }

  /* ════════════════════════════════════
     정보 수정 폼 제출 (PUT /api/student/{encryptedNo})
  ════════════════════════════════════ */
  function submitEditForm(e) {
    e.preventDefault();
    if (!validateEditForm()) {
      const firstErr = document.querySelector('#basic-edit-form .is-invalid, #basic-edit-form .invalid-feedback.show');
      if (firstErr) firstErr.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }

    const targets = [];
    document.querySelectorAll('#basic-edit-targetList .target-item').forEach(function (item) {
      const idx = parseInt(item.getAttribute('data-index'), 10);
      const univ = item.querySelector('.target-univ').value.trim();
      const major = item.querySelector('.target-major').value.trim();
      if (univ && major) targets.push({ ranking: idx + 1, univ: univ, major: major });
    });

    const payload = {
      studentRegistor: {
        name: document.getElementById('basic-edit-name').value.trim(),
        schoolCode: parseInt(document.getElementById('basic-edit-schoolCode').value, 10),
        grade: parseInt(document.querySelector('input[name="basic-edit-grade"]:checked').value, 10),
        semester: parseInt(document.querySelector('input[name="basic-edit-semester"]:checked').value, 10),
        track: document.querySelector('input[name="basic-edit-track"]:checked').value,
        target: targets
      }
    };

    const submitBtn = document.getElementById('basic-edit-submit');
    submitBtn.disabled = true;

    const params = {
      name: document.getElementById('basic-edit-name').value.trim(),
      schoolCode: document.getElementById('basic-edit-schoolCode').value,
      grade: document.querySelector('input[name="basic-edit-grade"]:checked').value,
      semester: document.querySelector('input[name="basic-edit-semester"]:checked').value,
      track: document.querySelector('input[name="basic-edit-track"]:checked').value,
    };

    // target 배열을 인덱스 파라미터로 변환
    document.querySelectorAll('#basic-edit-targetList .target-item').forEach(function (item) {
      const idx = parseInt(item.getAttribute('data-index'), 10);
      const univ = item.querySelector('.target-univ').value.trim();
      const major = item.querySelector('.target-major').value.trim();
      if (univ && major) {
        params['target[' + idx + '].ranking'] = idx + 1;
        params['target[' + idx + '].univ'] = univ;
        params['target[' + idx + '].major'] = major;
      }
    });

    $.ajax({
      url: '/api/student/' + ENCRYPTED_NO,
      method: 'PUT',
      data: params,
      success: function () {
        location.reload();
      },
      error: function () {
        alert('수정 중 오류가 발생했습니다.');
        submitBtn.disabled = false;
      }
    });
  }

  /* ════════════════════════════════════
     상태 수정 모달 열기/닫기
  ════════════════════════════════════ */
  function openStatusModal() {
    const current = document.getElementById('basic-btn-status-edit').dataset.current;
    const radio = document.getElementById('basic-status-' + current);
    if (radio) radio.checked = true;
    hideErr('basic-status-error');
    document.getElementById('basic-status-modal').style.display = 'flex';
    lucide.createIcons();
  }

  function closeStatusModal() {
    document.getElementById('basic-status-modal').style.display = 'none';
  }

  /* ════════════════════════════════════
     상태 수정 요청 (PUT /api/student/{encryptedNo}/status)
  ════════════════════════════════════ */
  function submitStatusChange() {
    const selected = document.querySelector('input[name="basic-status-radio"]:checked');
    if (!selected) {
      showErr('basic-status-error', '변경할 상태를 선택해 주세요.');
      document.getElementById('basic-status-error').classList.add('show');
      return;
    }

    const confirmBtn = document.getElementById('basic-status-confirm');
    confirmBtn.disabled = true;

    $.ajax({
      url: '/api/student/' + ENCRYPTED_NO + '/status',
      method: 'PUT',
      data: { status: selected.value },
      success: function () {
        location.reload();
      },
      error: function () {
        alert('상태 변경 중 오류가 발생했습니다.');
        confirmBtn.disabled = false;
      }
    });
  }

  /* ════════════════════════════════════
     이벤트 바인딩
  ════════════════════════════════════ */
  document.addEventListener('DOMContentLoaded', function () {

    // 정보 수정 모달
    const btnUpdate = document.getElementById('basic-btn-update');
    if (btnUpdate) btnUpdate.addEventListener('click', openEditModal);

    const editForm = document.getElementById('basic-edit-form');
    if (editForm) editForm.addEventListener('submit', submitEditForm);

    const editCancel = document.getElementById('basic-edit-cancel');
    if (editCancel) editCancel.addEventListener('click', closeEditModal);

    const editClose = document.getElementById('basic-edit-modal-close');
    if (editClose) editClose.addEventListener('click', closeEditModal);

    // 학교 검색 버튼 (수정 모달)
    const btnSearchSchool = document.getElementById('basic-edit-btnSearchSchool');
    if (btnSearchSchool) btnSearchSchool.addEventListener('click', searchEditSchool);

    const btnAddSchool = document.getElementById('basic-edit-btnAddSchool');
    if (btnAddSchool) btnAddSchool.addEventListener('click', openAddEditSchool);

    const schoolInput = document.getElementById('basic-edit-schoolNameInput');
    if (schoolInput) {
      schoolInput.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') { e.preventDefault(); searchEditSchool(); }
      });
    }

    // 패널 외부 클릭 시 닫기
    document.addEventListener('click', function (e) {
      const panel = document.getElementById('basic-edit-schoolPanel');
      const display = document.getElementById('basic-edit-schoolDisplay');
      if (panel && panel.style.display === 'block'
        && !panel.contains(e.target)
        && display && !display.contains(e.target)) {
        basicEditCloseSchoolPanel();
      }
    });

    // 이름 실시간 에러 제거
    const nameInput = document.getElementById('basic-edit-name');
    if (nameInput) {
      nameInput.addEventListener('input', function () {
        if (REGEXP.NAME.test(this.value.trim())) {
          this.classList.remove('is-invalid');
          hideErr('basic-edit-nameError');
        }
      });
    }

    // 대학/전공 실시간 에러 제거
    document.querySelectorAll('#basic-edit-targetList .target-univ, #basic-edit-targetList .target-major')
      .forEach(function (input) {
        input.addEventListener('input', function () { clearTargetFieldError(this); });
      });

    // 상태 수정 모달
    const btnStatusEdit = document.getElementById('basic-btn-status-edit');
    if (btnStatusEdit) btnStatusEdit.addEventListener('click', openStatusModal);

    const statusCancel = document.getElementById('basic-status-cancel');
    if (statusCancel) statusCancel.addEventListener('click', closeStatusModal);

    const statusClose = document.getElementById('basic-status-modal-close');
    if (statusClose) statusClose.addEventListener('click', closeStatusModal);

    const statusConfirm = document.getElementById('basic-status-confirm');
    if (statusConfirm) statusConfirm.addEventListener('click', submitStatusChange);

    // 모달 배경 클릭 시 닫기
    document.addEventListener('click', function (e) {
      if (e.target.id === 'basic-edit-modal') closeEditModal();
      if (e.target.id === 'basic-status-modal') closeStatusModal();
    });

  });

})();