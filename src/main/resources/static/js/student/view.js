(function () {

  /* ── 네비 클릭 이벤트 ── */
  document.querySelectorAll('.detail-nav__item').forEach(function (navItem) {
    navItem.addEventListener('click', function (e) {
      e.preventDefault();

      const href = this.getAttribute('href');
      if (!href) return;

      // '기본 정보'로 돌아올 때는 새로고침
      const isBasicTab = href.match(/\/view\/[^/]+$/);
      if (isBasicTab) {
        location.href = href;
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
      },
      error: function () {
        alert('페이지를 불러오는 중 오류가 발생했습니다.');
      }
    });
  }

  /* ── 삭제 버튼 (ADMIN) ── */
  document.addEventListener('click', function (e) {
    // 삭제 버튼 클릭
    if (e.target.closest('#basic-btn-delete')) {
      document.getElementById('basic-delete-modal').style.display = 'flex';
    }

    // 취소
    if (e.target.closest('#basic-delete-cancel')) {
      document.getElementById('basic-delete-modal').style.display = 'none';
    }

    // 삭제 확인
    if (e.target.closest('#basic-delete-confirm')) {
      const btn = document.getElementById('basic-btn-delete');
      const deleteUrl = btn ? btn.dataset.url : null;
      if (!deleteUrl) return;

      $.ajax({
        url: deleteUrl,
        method: 'POST',
        success: function () {
          alert('학생이 삭제되었습니다.');
          history.back();
        },
        error: function () {
          alert('삭제 중 오류가 발생했습니다.');
          document.getElementById('basic-delete-modal').style.display = 'none';
        }
      });
    }

    // 모달 바깥 클릭 시 닫기
    if (e.target.id === 'basic-delete-modal') {
      e.target.style.display = 'none';
    }
  });

})();