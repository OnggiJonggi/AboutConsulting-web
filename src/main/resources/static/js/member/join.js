(function () {

  // ── 정규식 ──────────────────────────────────────────────
  const REGEXP = {
    id: /^[A-Za-z0-9]{4,12}$/,
    pwd: /^[A-Za-z0-9@$!%*#?&]{4,20}$/,
    name: /^[ㄱ-ㅎ가-힣a-zA-Z0-9]{1,10}$/
  };

  // ── 중복확인 통과 여부 플래그 ──────────────────────────
  let idChecked = false;
  let nicknameChecked = false;

  // ── 헬퍼: 에러 메시지 표시/숨김 ──────────────────────
  function showError(elId, msg) {
    const el = document.getElementById(elId);
    if (!el) return;
    el.textContent = msg;
    el.style.display = msg ? '' : 'none';
  }

  function clearCheckMsg(elId) {
    const el = document.getElementById(elId);
    if (!el) return;
    el.textContent = '';
    el.style.display = 'none';
    el.className = 'field-check-msg';
  }

  function showCheckMsg(elId, msg, isOk) {
    const el = document.getElementById(elId);
    if (!el) return;
    el.textContent = msg;
    el.style.display = '';
    el.className = 'field-check-msg ' + (isOk ? 'is-ok' : 'is-fail');
  }

  // ── 단일 필드 유효성 검사 ─────────────────────────────
  function validateField(inputEl, regexp, errorElId, emptyMsg, invalidMsg) {
    const val = inputEl.value.trim();
    if (!val) {
      showError(errorElId, emptyMsg);
      inputEl.classList.add('input--error');
      return false;
    }
    if (!regexp.test(val)) {
      showError(errorElId, invalidMsg);
      inputEl.classList.add('input--error');
      return false;
    }
    showError(errorElId, '');
    inputEl.classList.remove('input--error');
    return true;
  }

  // ── 비밀번호 보이기/숨기기 ──────────────────────────
  document.querySelectorAll('.btn-toggle-pw').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const targetId = btn.dataset.target;
      const input = document.getElementById(targetId);
      const icon = btn.querySelector('i');
      if (!input) return;
      if (input.type === 'password') {
        input.type = 'text';
        icon.setAttribute('data-lucide', 'eye-off');
      } else {
        input.type = 'password';
        icon.setAttribute('data-lucide', 'eye');
      }
      lucide.createIcons();
    });
  });

  // ── 아이디 입력 변경 시 중복확인 초기화 ──────────────
  document.getElementById('userId').addEventListener('input', function () {
    idChecked = false;
    clearCheckMsg('userIdCheckMsg');
    showError('userIdError', '');
    this.classList.remove('input--error');
  });

  // ── 닉네임 입력 변경 시 중복확인 초기화 ─────────────
  document.getElementById('nickname').addEventListener('input', function () {
    nicknameChecked = false;
    clearCheckMsg('nicknameCheckMsg');
    showError('nicknameError', '');
    this.classList.remove('input--error');
  });

  // ── 아이디 중복확인 ──────────────────────────────────
  document.getElementById('btnCheckId').addEventListener('click', function () {
    const input = document.getElementById('userId');
    const valid = validateField(input, REGEXP.id, 'userIdError',
      '아이디를 입력해 주세요.',
      '아이디는 영문·숫자 4~12자로 입력해 주세요.');

    if (!valid) { idChecked = false; return; }

    const btn = this;
    btn.disabled = true;

    $.ajax({
      url: '/api/member/check-id',
      type: 'GET',
      data: { userId: input.value.trim() },
      statusCode: {
        200: function () {
          idChecked = true;
          showCheckMsg('userIdCheckMsg', '✓ 사용 가능한 아이디입니다.', true);
          showError('userIdError', '');
        },
        400: function () {
          idChecked = false;
          showCheckMsg('userIdCheckMsg', '✗ 이미 사용 중인 아이디입니다.', false);
        }
      },
      error: function (xhr) {
        idChecked = false;
        if (xhr.status === 500) {
          showCheckMsg('userIdCheckMsg', '✗ 이미 사용 중인 아이디입니다.', false);
        } else {
          showCheckMsg('userIdCheckMsg', '오류가 발생했습니다. 다시 시도해 주세요.', false);
        }
      },
      complete: function () {
        btn.disabled = false;
      }
    });
  });

  // ── 닉네임 중복확인 ─────────────────────────────────
  document.getElementById('btnCheckNickname').addEventListener('click', function () {
    const input = document.getElementById('nickname');
    const valid = validateField(input, REGEXP.name, 'nicknameError',
      '닉네임을 입력해 주세요.',
      '닉네임은 한글·영문·숫자 1~10자로 입력해 주세요.');

    if (!valid) { nicknameChecked = false; return; }

    const btn = this;
    btn.disabled = true;

    $.ajax({
      url: '/api/member/check-nickname',
      type: 'GET',
      data: { nickname: input.value.trim() },
      statusCode: {
        200: function () {
          nicknameChecked = true;
          showCheckMsg('nicknameCheckMsg', '✓ 사용 가능한 닉네임입니다.', true);
          showError('nicknameError', '');
        },
        500: function () {
          nicknameChecked = false;
          showCheckMsg('nicknameCheckMsg', '✗ 이미 사용 중인 닉네임입니다.', false);
        }
      },
      error: function (xhr) {
        nicknameChecked = false;
        if (xhr.status === 500) {
          showCheckMsg('nicknameCheckMsg', '✗ 이미 사용 중인 닉네임입니다.', false);
        } else {
          showCheckMsg('nicknameCheckMsg', '오류가 발생했습니다. 다시 시도해 주세요.', false);
        }
      },
      complete: function () {
        btn.disabled = false;
      }
    });
  });

  // ── 폼 제출 유효성 검사 ──────────────────────────────
  document.getElementById('joinForm').addEventListener('submit', function (e) {
    let ok = true;

    // 아이디
    const idInput = document.getElementById('userId');
    if (!validateField(idInput, REGEXP.id, 'userIdError',
      '아이디를 입력해 주세요.',
      '아이디는 영문·숫자 4~12자로 입력해 주세요.')) {
      ok = false;
    } else if (!idChecked) {
      showError('userIdError', '아이디 중복확인을 완료해 주세요.');
      idInput.classList.add('input--error');
      ok = false;
    }

    // 비밀번호
    if (!validateField(
      document.getElementById('userPwd'), REGEXP.pwd, 'userPwdError',
      '비밀번호를 입력해 주세요.',
      '비밀번호는 영문·숫자·특수문자(@$!%*#?&) 4~20자로 입력해 주세요.')) {
      ok = false;
    }

    // 이름
    if (!validateField(
      document.getElementById('name'), REGEXP.name, 'nameError',
      '이름을 입력해 주세요.',
      '이름은 한글·영문·숫자 1~10자로 입력해 주세요.')) {
      ok = false;
    }

    // 닉네임
    const nicknameInput = document.getElementById('nickname');
    if (!validateField(nicknameInput, REGEXP.name, 'nicknameError',
      '닉네임을 입력해 주세요.',
      '닉네임은 한글·영문·숫자 1~10자로 입력해 주세요.')) {
      ok = false;
    } else if (!nicknameChecked) {
      showError('nicknameError', '닉네임 중복확인을 완료해 주세요.');
      nicknameInput.classList.add('input--error');
      ok = false;
    }

    if (!ok) {
      e.preventDefault();
      // 첫 번째 에러 필드로 포커스
      const firstError = document.querySelector('.input--error');
      if (firstError) firstError.focus();
    }
  });

})();