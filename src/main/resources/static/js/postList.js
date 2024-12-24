// 모든 postitLike 요소 가져오기
const likeIcons = document.querySelectorAll(".postitLike");

// 각 요소에 클릭 이벤트 추가
likeIcons.forEach((icon) => {
  icon.addEventListener("click", async (e) => {
    // 로그인 상태가 아닌 경우 동작 X
    if (loginMemberNo == null) {
      alert("로그인 후 이용해 주세요");
      return;
    }

    // 데이터 속성으로 게시글 번호 확인
    let likeCheck = e.target.getAttribute("data-like-status");
    const postNo = e.target.getAttribute("data-post-no");

    // 준비된 3개의 변수를 객체로 저장 (JSON 변환 예정)

    console.log("원래 좋아요 수 : ",likeCheck);

    const obj = {
      memberNo: loginMemberNo,
      postNo: postNo,
      postLikeCheck: likeCheck,
    };

    // 좋아요 INSERT/DELETE 비동기 요청
    const response = await fetch("/post/like", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(obj),
    });

    // 서버에서 반환된 결과를 텍스트로 변환
    const count = await response.text();

    // 반환값이 0인 경우 에러 처리
    if (count == -1) {
      console.log("좋아요 처리 실패");
      return;
    }

    //postLikeCheck 값 0 <-> 1 변환
    likeCheck = likeCheck == 0 ? 1 : 0;

    console.log("변경후 좋아요 수 : ", likeCheck);

    // 하트를 채웠다/비웠다 바꾸기
    e.target.classList.toggle("fa-regular");
    e.target.classList.toggle("fa-solid");

    // data-like-status 속성 업데이트
    e.target.setAttribute("data-like-status", likeCheck);
  });
});

// 글쓰기 버튼
const insertBtn = document.querySelector("#insertBtn");

if(insertBtn) {
  insertBtn.addEventListener("click", () => {
    location.href = "/post/insert"
  });
}