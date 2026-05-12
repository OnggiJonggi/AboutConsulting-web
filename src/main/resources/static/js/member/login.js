(function () {
  const REGEX_ID = /^[A-Za-z0-9]{4,12}$/;
  const REGEX_PWD = /^[A-Za-z0-9@$!%*#?&]{4,20}$/;

  const userIdInput = document.getElementById('userId');
  const userPwdInput = document.getElementById('userPwd');
  const feedbackId = document.getElementById('feedback-userId');
  const feedbackPwd = document.getElementById('feedback-userPwd');
  const toggleBtn = document.getElementById('togglePwd');
  const toggleIcon = document.getElementById('togglePwdIcon');
  const loginForm = document.getElementById('loginForm');

  /* ── 유효성 상태 표시 헬퍼 ── */
  function setValid(input, feedback) {
    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    feedback.textContent = '';
    feedback.className = 'form-feedback success';
  }

  function setInvalid(input, feedback, message) {
    input.classList.remove('is-valid');
    input.classList.add('is-invalid');
    feedback.textContent = message;
    feedback.className = 'form-feedback error';
  }

  function clearState(input, feedback) {
    input.classList.remove('is-valid', 'is-invalid');
    feedback.textContent = '';
    feedback.className = 'form-feedback';
  }

  /* ── 아이디 유효성 검사 ── */
  function validateUserId() {
    const val = userIdInput.value.trim();
    if (val === '') {
      clearState(userIdInput, feedbackId);
      return false;
    }
    if (!REGEX_ID.test(val)) {
      setInvalid(userIdInput, feedbackId, '영문 또는 숫자 4~12자로 입력해주세요.');
      return false;
    }
    setValid(userIdInput, feedbackId);
    return true;
  }

  /* ── 비밀번호 유효성 검사 ── */
  function validateUserPwd() {
    const val = userPwdInput.value;
    if (val === '') {
      clearState(userPwdInput, feedbackPwd);
      return false;
    }
    if (!REGEX_PWD.test(val)) {
      setInvalid(userPwdInput, feedbackPwd, '영문·숫자·특수문자(@$!%*#?&) 4~20자로 입력해 주세요.');
      return false;
    }
    setValid(userPwdInput, feedbackPwd);
    return true;
  }

  /* ── 이벤트: 실시간 검사 ── */
  userIdInput.addEventListener('input', validateUserId);
  userIdInput.addEventListener('blur', function () {
    if (this.value.trim() === '') clearState(this, feedbackId);
    else validateUserId();
  });

  userPwdInput.addEventListener('input', validateUserPwd);
  userPwdInput.addEventListener('blur', function () {
    if (this.value === '') clearState(this, feedbackPwd);
    else validateUserPwd();
  });

  /* ── 비밀번호 표시 토글 ── */
  toggleBtn.addEventListener('click', function () {
    const isPassword = userPwdInput.type === 'password';
    userPwdInput.type = isPassword ? 'text' : 'password';
    // lucide 아이콘 교체
    toggleIcon.setAttribute('data-lucide', isPassword ? 'eye-off' : 'eye');
    lucide.createIcons();
  });

  /* ── 폼 제출 전 최종 유효성 검사 ── */
  loginForm.addEventListener('submit', function (e) {
    const idOk = validateUserId();
    const pwdOk = validateUserPwd();

    if (!idOk || !pwdOk) {
      e.preventDefault();
      if (!idOk) userIdInput.focus();
      else userPwdInput.focus();
    }
  });

})();