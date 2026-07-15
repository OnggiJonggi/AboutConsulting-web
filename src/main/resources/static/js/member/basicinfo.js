(function () {

  /* ── 정규식 ── */
  const REGEXP = {
    name: /^[ㄱ-ㅎ가-힣a-zA-Z0-9]{1,10}$/,
    pwd: /^[A-Za-z0-9@$!%*#?&]{4,20}$/,
    phone: /^(01[016789]|02|0[3-9][0-9])-\d{3,4}-\d{4}$/
  };

  /* ── 플래그 ── */
  let nicknameChecked = false;

  /* ── 헬퍼 ── */
  function showError(id, msg) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = msg;
    el.style.display = msg ? '' : 'none';
  }

  function clearCheckMsg(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = '';
    el.style.display = 'none';
    el.className = 'field-check-msg';
  }

  function showCheckMsg(id, msg, isOk) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = msg;
    el.style.display = '';
    el.className = 'field-check-msg ' + (isOk ? 'is-ok' : 'is-fail');
  }

  function validateField(inputEl, regexp, errorId, emptyMsg, invalidMsg) {
    const val = inputEl.value.trim();
    if (!val) {
      showError(errorId, emptyMsg);
      inputEl.classList.add('input--error');
      return false;
    }
    if (!regexp.test(val)) {
      showError(errorId, invalidMsg);
      inputEl.classList.add('input--error');
      return false;
    }
    showError(errorId, '');
    inputEl.classList.remove('input--error');
    return true;
  }

  /* ── 전화번호 초기값 분리 ── */
  (function initPhone() {
    if (!ORIGINAL_PHONE) return;
    const parts = ORIGINAL_PHONE.split('-');
    if (parts.length === 3) {
      document.getElementById('editPhone1').value = parts[0];
      document.getElementById('editPhone2').value = parts[1];
      document.getElementById('editPhone3').value = parts[2];
    }
  })();

  /* ── 전화번호 숫자만 입력 ── */
  ['editPhone1', 'editPhone2', 'editPhone3'].forEach(function (id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.addEventListener('input', function () {
      this.value = this.value.replace(/[^0-9]/g, '');
      showError('editPhoneError', '');
      this.classList.remove('input--error');
    });
  });

  /* ── 전화번호 조합 및 유효성 검사 ── */
  function getPhoneValue() {
    const p1 = document.getElementById('editPhone1').value.trim();
    const p2 = document.getElementById('editPhone2').value.trim();
    const p3 = document.getElementById('editPhone3').value.trim();
    return p1 + '-' + p2 + '-' + p3;
  }

  function validatePhone() {
    const phone = getPhoneValue();
    const p1El = document.getElementById('editPhone1');
    const p2El = document.getElementById('editPhone2');
    const p3El = document.getElementById('editPhone3');
    if (!p1El.value.trim() || !p2El.value.trim() || !p3El.value.trim()) {
      showError('editPhoneError', '전화번호를 입력해 주세요.');
      [p1El, p2El, p3El].forEach(function (el) { el.classList.add('input--error'); });
      return false;
    }
    if (!REGEXP.phone.test(phone)) {
      showError('editPhoneError', '올바른 전화번호 형식이 아닙니다.');
      [p1El, p2El, p3El].forEach(function (el) { el.classList.add('input--error'); });
      return false;
    }
    showError('editPhoneError', '');
    [p1El, p2El, p3El].forEach(function (el) { el.classList.remove('input--error'); });
    return true;
  }

  /* ── 비밀번호 보이기/숨기기 ── */
  document.querySelectorAll('.btn-toggle-pw').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const input = document.getElementById(btn.dataset.target);
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

  /* ── 정보 수정 모달 열기/닫기 ── */
  function openUpdateModal() {
    // 별명 초기화 시 중복확인 플래그 리셋
    const currentNickname = document.getElementById('editNickname').value.trim();
    nicknameChecked = (currentNickname === ORIGINAL_NICKNAME);
    document.getElementById('updateModal').style.display = 'flex';
  }

  function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
    showError('updateFormError', '');
  }

  document.getElementById('btnOpenUpdateModal').addEventListener('click', openUpdateModal);
  document.getElementById('btnCloseUpdateModal').addEventListener('click', closeUpdateModal);
  document.getElementById('btnCancelUpdate').addEventListener('click', closeUpdateModal);

  document.getElementById('updateModal').addEventListener('click', function (e) {
    if (e.target === this) closeUpdateModal();
  });

  /* ── 별명 입력 변경 시 중복확인 초기화 ── */
  document.getElementById('editNickname').addEventListener('input', function () {
    // 원래 닉네임과 동일하면 중복확인 불필요
    nicknameChecked = (this.value.trim() === ORIGINAL_NICKNAME);
    clearCheckMsg('editNicknameCheckMsg');
    showError('editNicknameError', '');
    this.classList.remove('input--error');
  });

  /* ── 별명 중복 확인 ── */
  document.getElementById('btnCheckNickname').addEventListener('click', function () {
    const input = document.getElementById('editNickname');
    const valid = validateField(input, REGEXP.name, 'editNicknameError',
      '별명을 입력해 주세요.',
      '별명은 한글·영문·숫자 1~10자로 입력해 주세요.');
    if (!valid) { nicknameChecked = false; return; }

    // 원래 닉네임과 동일하면 중복확인 통과
    if (input.value.trim() === ORIGINAL_NICKNAME) {
      nicknameChecked = true;
      showCheckMsg('editNicknameCheckMsg', '✓ 현재 사용 중인 닉네임입니다.', true);
      return;
    }

    const btn = this;
    btn.disabled = true;
    $.ajax({
      url: '/api/member/check-updatednickname',
      type: 'GET',
      data: { nickname: input.value.trim(), encMemberNo: ENCRYPTED_MEMBER_NO },
      statusCode: {
        200: function () {
          nicknameChecked = true;
          showCheckMsg('editNicknameCheckMsg', '✓ 사용 가능한 닉네임입니다.', true);
          showError('editNicknameError', '');
        },
        500: function () {
          nicknameChecked = false;
          showCheckMsg('editNicknameCheckMsg', '✗ 이미 사용 중인 닉네임입니다.', false);
        }
      },
      error: function (xhr) {
        nicknameChecked = false;
        if (xhr.status === 500) {
          showCheckMsg('editNicknameCheckMsg', '✗ 이미 사용 중인 닉네임입니다.', false);
        } else {
          showCheckMsg('editNicknameCheckMsg', '오류가 발생했습니다. 다시 시도해 주세요.', false);
        }
      },
      complete: function () { btn.disabled = false; }
    });
  });

  /* ── 정보 수정 요청 ── */
  document.getElementById('btnConfirmUpdate').addEventListener('click', function () {
    let ok = true;

    if (!validateField(
      document.getElementById('editName'), REGEXP.name, 'editNameError',
      '이름을 입력해 주세요.',
      '이름은 한글·영문·숫자 1~10자로 입력해 주세요.')) { ok = false; }

    const nicknameInput = document.getElementById('editNickname');
    if (!validateField(nicknameInput, REGEXP.name, 'editNicknameError',
      '별명을 입력해 주세요.',
      '별명은 한글·영문·숫자 1~10자로 입력해 주세요.')) {
      ok = false;
    } else if (!nicknameChecked) {
      showError('editNicknameError', '닉네임 중복확인을 완료해 주세요.');
      nicknameInput.classList.add('input--error');
      ok = false;
    }

    if (!validatePhone()) { ok = false; }

    const pwdVal = document.getElementById('editUserPwd').value;
    const pwdConfirm = document.getElementById('editUserPwdConfirm').value;
    let sendPwd = '';

    if (pwdVal) {
      if (!REGEXP.pwd.test(pwdVal)) {
        showError('editUserPwdError', '비밀번호는 영문·숫자·특수문자(@$!%*#?&) 4~20자로 입력해 주세요.');
        document.getElementById('editUserPwd').classList.add('input--error');
        ok = false;
      } else {
        showError('editUserPwdError', '');
        document.getElementById('editUserPwd').classList.remove('input--error');
      }

      if (pwdVal !== pwdConfirm) {
        showError('editUserPwdConfirmError', '비밀번호가 일치하지 않습니다.');
        document.getElementById('editUserPwdConfirm').classList.add('input--error');
        ok = false;
      } else {
        showError('editUserPwdConfirmError', '');
        document.getElementById('editUserPwdConfirm').classList.remove('input--error');
        sendPwd = pwdVal;
      }
    } else {
      showError('editUserPwdError', '');
      showError('editUserPwdConfirmError', '');
      document.getElementById('editUserPwd').classList.remove('input--error');
      document.getElementById('editUserPwdConfirm').classList.remove('input--error');
    }

    if (!ok) return;

    const payload = {
      encMemberNo: ENCRYPTED_MEMBER_NO,
      name: document.getElementById('editName').value.trim(),
      nickname: document.getElementById('editNickname').value.trim(),
      phone: getPhoneValue()
    };
    if (sendPwd) { payload.userPwd = sendPwd; }

    $.ajax({
      url: '/api/member/' + ENCRYPTED_MEMBER_NO + '/update',
      type: 'PUT',
      data: {
        encMemberNo: ENCRYPTED_MEMBER_NO,
        name: document.getElementById('editName').value.trim(),
        nickname: document.getElementById('editNickname').value.trim(),
        phone: getPhoneValue(),
        userPwd: sendPwd
      },
      statusCode: {
        200: function () {
          location.reload();
        }
      },
      error: function () {
        showError('updateFormError', '수정에 실패했습니다.');
      }
    });
  });

  /* ── 권한 수정 ── */
  const btnOpenRoleEdit = document.getElementById('btnOpenRoleEdit');
  const btnCancelRoleEdit = document.getElementById('btnCancelRoleEdit');
  const btnConfirmRoleEdit = document.getElementById('btnConfirmRoleEdit');
  const roleEditBox = document.getElementById('roleEditBox');

  if (btnOpenRoleEdit) {
    btnOpenRoleEdit.addEventListener('click', function () {
      roleEditBox.style.display = roleEditBox.style.display === 'none' ? 'block' : 'none';
      showError('roleEditError', '');
    });
  }

  if (btnCancelRoleEdit) {
    btnCancelRoleEdit.addEventListener('click', function () {
      roleEditBox.style.display = 'none';
      showError('roleEditError', '');
    });
  }

  if (btnConfirmRoleEdit) {
    btnConfirmRoleEdit.addEventListener('click', function () {
      const selected = document.querySelector('input[name="roleSelect"]:checked');
      if (!selected) {
        showError('roleEditError', '권한을 선택해 주세요.');
        return;
      }
      $.ajax({
        url: '/api/member/' + ENCRYPTED_MEMBER_NO + '/update/role',
        type: 'PUT',
        data: {
          encMemberNo: ENCRYPTED_MEMBER_NO,
          role: selected.value
        },
        statusCode: {
          200: function () { location.reload(); },
          403: function () { showError('roleEditError', '수정 권한이 없습니다.'); }
        },
        error: function (xhr) {
          if (xhr.status === 403) {
            showError('roleEditError', '수정 권한이 없습니다.');
          } else {
            showError('roleEditError', '수정에 실패했습니다.');
          }
        }
      });
    });
  }

  /* ── 상태 수정 ── */
  const btnOpenStatusEdit = document.getElementById('btnOpenStatusEdit');
  const btnCancelStatusEdit = document.getElementById('btnCancelStatusEdit');
  const btnConfirmStatusEdit = document.getElementById('btnConfirmStatusEdit');
  const statusEditBox = document.getElementById('statusEditBox');

  if (btnOpenStatusEdit) {
    btnOpenStatusEdit.addEventListener('click', function () {
      statusEditBox.style.display = statusEditBox.style.display === 'none' ? 'block' : 'none';
      showError('statusEditError', '');
    });
  }

  if (btnCancelStatusEdit) {
    btnCancelStatusEdit.addEventListener('click', function () {
      statusEditBox.style.display = 'none';
      showError('statusEditError', '');
    });
  }

  if (btnConfirmStatusEdit) {
    btnConfirmStatusEdit.addEventListener('click', function () {
      const selected = document.querySelector('input[name="statusSelect"]:checked');
      if (!selected) {
        showError('statusEditError', '상태를 선택해 주세요.');
        return;
      }
      $.ajax({
        url: '/api/member/' + ENCRYPTED_MEMBER_NO + '/update/status',
        type: 'PUT',
        data: {
          encMemberNo: ENCRYPTED_MEMBER_NO,
          status: selected.value
        },
        statusCode: {
          200: function () { location.reload(); },
          403: function () { showError('statusEditError', '수정 권한이 없습니다.'); }
        },
        error: function (xhr) {
          if (xhr.status === 403) {
            showError('statusEditError', '수정 권한이 없습니다.');
          } else {
            showError('statusEditError', '수정에 실패했습니다.');
          }
        }
      });
    });
  }

  /* ── 회원 탈퇴 ── */
  const btnWithdraw = document.getElementById('btnWithdraw');
  if (btnWithdraw) {
    btnWithdraw.addEventListener('click', function () {
      document.getElementById('withdrawModal').style.display = 'flex';
    });
  }

  const btnWithdrawCancel = document.getElementById('btnWithdrawCancel');
  if (btnWithdrawCancel) {
    btnWithdrawCancel.addEventListener('click', function () {
      document.getElementById('withdrawModal').style.display = 'none';
    });
  }
})();