/* 제목, 내용 미작성 시 제출 불가 */
const form = document.querySelector(".insert-form");
form.addEventListener("submit", e => {

  // 제목, 내용 input 얻어오기
  const postTitle   = document.querySelector("#postTitle");
  const postContent = document.querySelector("#postContent");

  if(postTitle.value.trim().length === 0){
    alert("제목을 작성해주세요");
    postTitle.focus();
    e.preventDefault();
    return;
  }

  if(postContent.value.trim().length === 0){
    alert("내용을 작성해주세요");
    postContent.focus();
    e.preventDefault();
    return;
  }
})