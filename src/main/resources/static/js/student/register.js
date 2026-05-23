/* ═══════════════════════════════════════════
   상수 / 정규식
═══════════════════════════════════════════ */
const REGEXP = {
  NAME: /^[ㄱ-ㅎ가-힣]{1,10}$/,
  TRACK: /^[A-Za-z0-9ㄱ-ㅎ가-힣]{1,10}$/,
  TARGET_MAJOR: /^[A-Za-z0-9ㄱ-ㅎ가-힣\s/()]{1,20}$/,
  TARGET_UNIV: /^[A-Za-z0-9ㄱ-ㅎ가-힣\s·.,/()]{1,20}$/
};

/* ═══════════════════════════════════════════
   학교 패널 열기/닫기
═══════════════════════════════════════════ */
function openSchoolPanel() {
  document.getElementById('schoolPanel').style.display = 'block';
  document.getElementById('schoolNameInput').focus();
}

function closeSchoolPanel() {
  document.getElementById('schoolPanel').style.display = 'none';
  document.getElementById('schoolResultArea').style.display = 'none';
  document.getElementById('schoolResultArea').innerHTML = '';
}

/* ═══════════════════════════════════════════
   학교 검색 공통 렌더링
═══════════════════════════════════════════ */
function renderSchoolResults(data, mode) {
  // mode: 'search' | 'add'
  const area = document.getElementById('schoolResultArea');
  area.style.display = 'block';
  area.innerHTML = '';

  if (!data || data.length === 0) {
    area.innerHTML = `
      <div class="school-empty-msg">
        <p>검색된 학교가 없습니다. 학교를 추가해 주세요.</p>
      </div>`;
    return;
  }

  const table = document.createElement('table');
  table.className = 'school-table';
  table.innerHTML = `
    <thead>
      <tr>
        <th>학교명</th>
        <th>시/도</th>
        <th>시/군/구</th>
        <th>공학여부</th>
        <th>특목고</th>
        <th>특목고 종류</th>
      </tr>
    </thead>
    <tbody id="schoolTableBody"></tbody>`;

  const tbody = table.querySelector('#schoolTableBody');

  data.forEach(school => {
    const tr = document.createElement('tr');
    tr.className = 'school-table-row';
    const specialized = school.specialized ? school.specialized : '일반고';
    const sigungu = school.sigungu ? school.sigungu : '-';
    const specializedType = school.specializedType ? school.specializedType : '-';

    tr.innerHTML = `
      <td>${escapeHtml(school.name)}</td>
      <td>${escapeHtml(school.sido)}</td>
      <td>${escapeHtml(sigungu)}</td>
      <td>${escapeHtml(school.coeducation)}</td>
      <td>${escapeHtml(specialized)}</td>
      <td>${escapeHtml(specializedType)}</td>`;

    tr.addEventListener('click', () => {
      if (mode === 'search') {
        selectSchool(school.schoolCode, school.name);
      } else {
        confirmAddSchool(school.schoolCode, school.name);
      }
    });

    tbody.appendChild(tr);
  });

  area.appendChild(table);
}

/* ═══════════════════════════════════════════
   학교 검색 (GET /api/school/search)
═══════════════════════════════════════════ */
function searchSchool() {
  const schoolName = document.getElementById('schoolNameInput').value.trim();
  if (!schoolName) {
    showSchoolMsg('학교 이름을 입력하세요.');
    return;
  }
  $.ajax({
    url: '/api/school/search',
    method: 'GET',
    data: { schoolName: schoolName },
    success: function (data) {
      renderSchoolResults(data, 'search');
    },
    error: function () {
      showSchoolMsg('검색 중 오류가 발생했습니다. 다시 시도해 주세요.');
    }
  });
}

/* ═══════════════════════════════════════════
   학교 추가 검색 (GET /api/school/search/open)
═══════════════════════════════════════════ */
function openAddSchool() {
  const schoolName = document.getElementById('schoolNameInput').value.trim();
  if (!schoolName) {
    showSchoolMsg('학교 이름을 입력하세요.');
    return;
  }
  $.ajax({
    url: '/api/school/search/open',
    method: 'GET',
    data: { schoolName: schoolName },
    success: function (data) {
      renderSchoolResults(data, 'add');
    },
    error: function () {
      showSchoolMsg('요청 중 오류가 발생했습니다. 다시 시도해 주세요.');
    }
  });
}

/* ═══════════════════════════════════════════
   학교 선택 (검색 모드)
═══════════════════════════════════════════ */
function selectSchool(schoolCode, schoolName) {
  document.getElementById('schoolCode').value = schoolCode;

  const display = document.getElementById('schoolDisplay');
  const text = document.getElementById('schoolDisplayText');
  text.textContent = schoolName;
  text.className = 'school-display-name';
  display.classList.add('has-value');

  hideError('schoolError');
  closeSchoolPanel();
  lucide.createIcons();
}

/* ═══════════════════════════════════════════
   학교 추가 확정 (POST /api/school/register)
═══════════════════════════════════════════ */
function confirmAddSchool(schoolCode, schoolName) {
  $.ajax({
    url: '/api/school/register',
    method: 'POST',
    data: { schoolCode: schoolCode },
    success: function () {
      selectSchool(schoolCode, schoolName);
    },
    statusCode: {
      400: function () {
        showSchoolMsg('세션이 만료되었습니다. 다시 검색해 주세요.');
      },
      404: function () {
        showSchoolMsg('학교 정보를 찾을 수 없습니다. 다시 검색해 주세요.');
      },
      409: function () {
        showSchoolMsg('이미 등록된 학교입니다. 다시 검색해 주세요.');
      }
    },
    error: function (xhr) {
      if (xhr.status !== 400 && xhr.status !== 404 && xhr.status !== 409) {
        showSchoolMsg('오류가 발생했습니다. 다시 시도해 주세요.');
      }
    }
  });
}

/* ═══════════════════════════════════════════
   유효성 검사 헬퍼
═══════════════════════════════════════════ */
function showError(id, msg) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = msg;
  el.classList.add('show');
  const input = el.previousElementSibling;
  if (input && input.classList.contains('form-control')) {
    input.classList.add('is-invalid');
  }
}

function hideError(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = '';
  el.classList.remove('show');
  const input = el.previousElementSibling;
  if (input && input.classList.contains('form-control')) {
    input.classList.remove('is-invalid');
  }
}

function showSchoolMsg(msg) {
  const area = document.getElementById('schoolResultArea');
  area.style.display = 'block';
  area.innerHTML = `<div class="school-empty-msg"><p>${escapeHtml(msg)}</p></div>`;
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
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

/* ═══════════════════════════════════════════
   폼 유효성 검사
═══════════════════════════════════════════ */
function validateForm() {
  let valid = true;

  // 이름
  const name = document.getElementById('name').value.trim();
  hideError('nameError');
  if (!name) {
    showError('nameError', '이름을 입력해 주세요.');
    document.getElementById('name').classList.add('is-invalid');
    valid = false;
  } else if (!REGEXP.NAME.test(name)) {
    showError('nameError', '이름은 한글 1~10자로 입력해 주세요.');
    document.getElementById('name').classList.add('is-invalid');
    valid = false;
  } else {
    document.getElementById('name').classList.remove('is-invalid');
  }

  // 학교
  const schoolCode = document.getElementById('schoolCode').value;
  hideError('schoolError');
  if (!schoolCode) {
    showError('schoolError', '학교를 선택해 주세요.');
    valid = false;
  }

  // 학년
  const grade = document.querySelector('input[name="grade"]:checked');
  hideError('gradeError');
  if (!grade) {
    showError('gradeError', '학년을 선택해 주세요.');
    valid = false;
  }

  // 학기
  const semester = document.querySelector('input[name="semester"]:checked');
  hideError('semesterError');
  if (!semester) {
    showError('semesterError', '학기를 선택해 주세요.');
    valid = false;
  }

  // 계열
  const track = document.querySelector('input[name="track"]:checked');
  hideError('trackError');
  if (!track) {
    showError('trackError', '계열을 선택해 주세요.');
    valid = false;
  }

  // 목표 대학/전공 쌍 검사
  hideError('targetPairError');
  const items = document.querySelectorAll('.target-item');
  let targetPairError = false;
  let completePairCount = 0;

  items.forEach(item => {
    const index = item.getAttribute('data-index');
    const univInput = item.querySelector('.target-univ');
    const majorInput = item.querySelector('.target-major');

    const rankingInput = item.querySelector(`input[name="target[${index}].ranking"]`);
    if (rankingInput) rankingInput.value = parseInt(index, 10) + 1;

    const univ = univInput.value.trim();
    const major = majorInput.value.trim();

    clearTargetFieldError(univInput);
    clearTargetFieldError(majorInput);

    // 둘 다 비어 있으면 일단 통과, 나중에 전체 개수 검사
    if (!univ && !major) return;

    // 둘 중 하나만 입력된 경우
    if (univ && !major) {
      showTargetFieldError(majorInput, '목표 전공도 함께 입력해 주세요.');
      targetPairError = true;
      valid = false;
      return;
    }

    if (!univ && major) {
      showTargetFieldError(univInput, '목표 대학도 함께 입력해 주세요.');
      targetPairError = true;
      valid = false;
      return;
    }

    // 둘 다 입력된 경우 형식 검사
    let rowValid = true;

    if (!REGEXP.TARGET_UNIV.test(univ)) {
      showTargetFieldError(univInput, '대학 이름은 한글/영문/숫자 1~20자로 입력해 주세요.');
      valid = false;
      rowValid = false;
    }

    if (!REGEXP.TARGET_MAJOR.test(major)) {
      showTargetFieldError(majorInput, '전공은 한글/영문/숫자 1~20자로 입력해 주세요.');
      valid = false;
      rowValid = false;
    }

    // 형식까지 정상인 완성 쌍만 카운트
    if (rowValid) {
      completePairCount++;
    }
  });

  // 최소 1쌍 이상 입력 여부 검사
  if (completePairCount === 0) {
    showError('targetPairError', '목표 대학/전공은 최소 1쌍 이상 입력해 주세요.');
    valid = false;
  }

  return valid;
}

/* ═══════════════════════════════════════════
   이벤트 바인딩
═══════════════════════════════════════════ */
document.addEventListener('DOMContentLoaded', function () {

  // 학교 검색 버튼
  document.getElementById('btnSearchSchool').addEventListener('click', searchSchool);

  // 학교 추가하기 버튼
  document.getElementById('btnAddSchool').addEventListener('click', openAddSchool);

  // 학교 이름 입력칸 Enter키
  document.getElementById('schoolNameInput').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') { e.preventDefault(); searchSchool(); }
  });

  // 패널 외부 클릭 시 닫기
  document.addEventListener('click', function (e) {
    const panel = document.getElementById('schoolPanel');
    const display = document.getElementById('schoolDisplay');
    if (panel.style.display === 'block'
      && !panel.contains(e.target)
      && !display.contains(e.target)) {
      closeSchoolPanel();
    }
  });

  // 폼 제출 시 유효성 검사
  document.getElementById('registerForm').addEventListener('submit', function (e) {
    if (!validateForm()) {
      e.preventDefault();
      // 첫 번째 에러 요소로 스크롤
      const firstErr = document.querySelector('.is-invalid, .invalid-feedback.show');
      if (firstErr) {
        firstErr.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }
  });

  // 실시간 입력 에러 제거 (이름)
  document.getElementById('name').addEventListener('input', function () {
    if (REGEXP.NAME.test(this.value.trim())) {
      this.classList.remove('is-invalid');
      hideError('nameError');
    }
  });

  // 실시간 대학/전공 에러 제거
  document.querySelectorAll('.target-univ, .target-major').forEach(input => {
    input.addEventListener('input', function () {
      clearTargetFieldError(this);
    });
  });

});