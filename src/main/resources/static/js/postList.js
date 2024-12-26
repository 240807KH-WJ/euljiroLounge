// 글쓰기 버튼
const insertBtn = document.querySelector("#insertBtn");

if (insertBtn) {
  insertBtn.addEventListener("click", () => {
    location.href = `/post/${postCode}/insert`;
  });
}

// 포스트잇 글쓰기 버튼
const postitBtn = document.querySelector("#postit-insertBtn");
const modal = document.querySelector(".postit-modal");
const closeModal = document.querySelector(".close-modal");
const textarea = document.querySelector("#postContent");
const charCount = document.querySelector("#currentCount");
const submitBtn = document.querySelector("#submitPostit");

if (postitBtn) {
  // 모달 열기
  postitBtn.addEventListener("click", () => {
    modal.classList.add("show");
    document.body.style.overflow = "hidden"; // 배경 스크롤 방지
  });

  // 모달 닫기
  closeModal.addEventListener("click", () => {
    modal.classList.remove("show");
    document.body.style.overflow = ""; // 스크롤 복구
    textarea.value = ""; // 텍스트 초기화
    charCount.textContent = "0"; // 글자수 초기화
  });

  // 모달 외부 클릭 시 닫기
  modal.addEventListener("click", (e) => {
    if (e.target === modal) {
      closeModal.click();
    }
  });

  // 글자수 카운트
  textarea.addEventListener("input", () => {
    const length = textarea.value.length;
    charCount.textContent = length;
  });

  // 포스트잇 등록
  submitBtn.addEventListener("click", async () => {
    const content = textarea.value.trim();

    if (!content) {
      alert("내용을 입력해주세요.");
      return;
    }

    try {
      const response = await fetch("/post/2/insert", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          postCode: postCode,
          memberNo: loginMemberNo,
          postTitle: `${loginMemberNo}번 회원이 붙인 포스트잇`,
          postContent: content,
        }),
      });

      const data = await response.text();

      if (data > 0) {
        alert("포스트잇이 등록되었습니다.");
        location.reload(); // 페이지 새로고침
      } else {
        alert("포스트잇 등록에 실패했습니다.");
      }
    } catch (err) {
      console.error("Error:", err);
      alert("포스트잇 등록 중 오류가 발생했습니다.");
    }
  });
}

// 포스트잇 클릭 시 상세 조회
const postitList = document.querySelectorAll(".postit-box");

// 포스트잇 상세 조회 모달 관련 요소
const detailModal = document.querySelector(".postit-detail-modal");
const closeDetailModal = document.querySelector(".close-detail-modal");
const writerName = document.querySelector(".writer-name");
const postDate = document.querySelector(".post-date");
const detailContent = document.querySelector(".detail-content");
const deleteBtn = document.querySelector("#deletePostit");
const detailLike = document.querySelector(".detail-like");
let likeCount = document.querySelector(".like-count");

let currentPostitLike = null; // 현재 클릭된 포스트잇의 하트 버튼
let currentPostitLikeCount = null; // 현재 클릭된 포스트잇의 좋아요 숫자

if (postitList) {
  postitList.forEach((postit) => {
    // 각 포스트잇 요소

    postit.addEventListener("click", (e) => {
      const postitLikeCount = postit.querySelector(".totalLikeCount"); // 좋아요 숫자 요소
      const postitLike = postit.querySelector(".postitLike");
      const postNo = postitLike.dataset.postNo;

      // 상세 조회 API 호출
      fetch(`/post/2/${postNo}`)
        .then((resp) => resp.json())
        .then((post) => {
          console.log(post);
          // 모달에 데이터 채우기
          writerName.textContent =
            post.memberNo == loginMemberNo ? "내가 쓴 글" : "익명의 사람";
          detailContent.textContent = post.postContent;
          postDate.textContent = post.postCreateDate;

          // 좋아요 상태 설정
          detailLike.className =
            "fa-heart detail-like " +
            (post.postLikeCheck != 0 ? "fa-solid" : "fa-regular");
          likeCount.textContent = post.postLikeCount;

          // 작성자인 경우 삭제 버튼 표시
          if (post.memberNo == loginMemberNo) {
            deleteBtn.classList.remove("hidden");
            deleteBtn.dataset.postNo = post.postNo;
          } else {
            deleteBtn.classList.add("hidden");
          }

          // 좋아요 버튼에 데이터 속성 추가
          detailLike.dataset.postNo = post.postNo;
          detailLike.dataset.likeStatus = post.postLikeCheck;

          // 현재 클릭한 포스트잇의 하트 버튼과 좋아요 숫자 참조 저장
          currentPostitLike = postitLike;
          currentPostitLikeCount = postitLikeCount;

          // 모달 표시
          detailModal.classList.add("show");
          document.body.style.overflow = "hidden";
        })
        .catch((err) => {
          console.error("Error:", err);
          alert("포스트잇 조회 중 오류가 발생했습니다.");
        });
    });
  });

  // 모달 닫기
  closeDetailModal.addEventListener("click", () => {
    detailModal.classList.remove("show");
    document.body.style.overflow = "";
  });

  // 모달 외부 클릭 시 닫기
  detailModal.addEventListener("click", (e) => {
    if (e.target === detailModal) {
      closeDetailModal.click();
    }
  });

  // 좋아요 버튼 클릭 시
  detailLike.addEventListener("click", async (e) => {
    // 로그인 상태가 아닌 경우 동작 X
    if (loginMemberNo == null) {
      alert("로그인 후 이용해 주세요");
      return;
    }

    // 준비된 3개의 변수를 객체로 저장 (JSON 변환 예정)
    // 데이터 속성으로 게시글 번호 확인
    let likeCheck = detailLike.dataset.likeStatus;
    const postNo = detailLike.dataset.postNo;

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

    // likeCount 값 +- 1
    let likeCountText = Number(likeCount.innerText);
    likeCount.innerText =
      likeCheck == 0 ? likeCountText + 1 : likeCountText - 1;

    // 클릭한 포스트잇의 좋아요 숫자도 업데이트
    let currentLikeCountText = Number(currentPostitLikeCount.innerText);
    currentPostitLikeCount.innerText = (likeCheck == 0 ? currentLikeCountText + 1 : currentLikeCountText - 1);

    //postLikeCheck 값 0 <-> 1 변환
    likeCheck = likeCheck == 0 ? 1 : 0;

    // 하트를 채웠다/비웠다 바꾸기
    e.target.classList.toggle("fa-regular");
    e.target.classList.toggle("fa-solid");

    // data-like-status 속성 업데이트
    e.target.setAttribute("data-like-status", likeCheck);

    // 클릭한 포스트잇의 하트 상태도 업데이트
    currentPostitLike.classList.toggle("fa-regular");
    currentPostitLike.classList.toggle("fa-solid");
    currentPostitLike.setAttribute("data-like-status", likeCheck);

    
  });
}
