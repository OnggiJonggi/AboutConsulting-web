/* =============================================
   어바웃컨설팅 메인 페이지 JS
   - Lucide 아이콘 초기화
   - 메뉴 카드 키보드 접근성 (Enter/Space)
   ============================================= */

document.addEventListener('DOMContentLoaded', function () {

  // Lucide 아이콘 렌더링
  if (typeof lucide !== 'undefined') {
    lucide.createIcons();
  }

  // 메뉴 카드 키보드 접근성
  document.querySelectorAll('.menu-card').forEach(function (card) {
    card.setAttribute('role', 'link');
    card.setAttribute('tabindex', '0');

    card.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        card.click();
      }
    });
  });

  // 닉네임 링크 키보드 접근성
  const nicknameLink = document.querySelector('.nickname-link');
  if (nicknameLink) {
    nicknameLink.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') {
        nicknameLink.click();
      }
    });
  }

});