/* 제목, 내용 미작성 시 제출 불가 */
const form = document.querySelector(".insert-form");
form.addEventListener("submit", (e) => {
  // 제목, 내용 input 얻어오기
  const postTitle = document.querySelector("#postTitle");
  const postContent = document.querySelector("#postContent");

  if (postTitle.value.trim().length === 0) {
    alert("제목을 작성해주세요");
    postTitle.focus();
    e.preventDefault();
    return;
  }

  if (postContent.value.trim().length === 0) {
    alert("내용을 작성해주세요");
    postContent.focus();
    e.preventDefault();
    return;
  }
});

/* 선택된 이미지 미리보기 관련 요소 얻어오기 */
let preview = document.querySelector(".preview"); // img 태그
let inputImage = document.querySelector(".inputImage"); // input 태그
let deleteImage = document.querySelector(".delete-image"); // x버튼
const MAX_SIZE = 1024 * 1024 * 5; // 최대 파일 크기 설정 (5MB) 바이트 단위로 나타내야함
// 1024B(byte) == 1KB
// 1024KB == 1MB
// 1024 * 1024 * 5 == 1MB에 5를 곱하면 5MB

let previousFile = null; // 이전에 선택된 파일을 저장
let previousPreview = null; // 이전에 선택된 파일을 저장

// 이미지 선택 시 미리보기 및 파일 크기 검사
inputImage.addEventListener("change", () => {
  console.log(inputImage.files); // FileList 0번 인덱스에 File 객체 있음

  const file = inputImage.files[0]; // 선택한 File 객체 가져오기
  console.log(file);

  if (file) {
    // 파일이 선택된 경우
    if (file.size <= MAX_SIZE) {
      // 파일 크기가 허용 범위 이내인 경우
      const newImageUrl = URL.createObjectURL(file); // 임시 URL 생성
      //console.log("임시 URL : " + newImageUrl); // blob:http://localhost:8080/05631bf1-ab54-4219-819f-64600ba28301
      preview.src = newImageUrl; // 미리보기 이미지 설정 (img 태그의 src에 선택한 파일 임시경로 대입)
      previousPreview = newImageUrl; // 선택된 이미지를 이전 이미지로 저장(다음에 바뀔일에 대비)
      previousFile = file; // 선택된 파일을 이전 파일로 저장(다음에 바뀔일에 대비)
      //console.log("선택된 파일 : " + inputImage.value); // C:\fakepath\철수.jfif
    } else {
      // 파일 크기가 허용 범위를 초과한 경우
      alert("5MB 이하의 이미지를 선택해 주세요.");
      inputImage.value = ""; // 1. 파일 선택 초기화 (alert 창은 띄웠지만 비우는건 따로 해야하는일..)
      // inputImage.files = null; 이것도 가능
      preview.src = previousPreview; // 2. 이전 미리보기 이미지로 복원(이전에 잘 선택했을때 저장해둔 이미지)

      // 3. 파일 입력 복구: 이전 파일이 존재하면 다시 할당
      if (previousFile) {
        const dataTransfer = new DataTransfer();
        // DataTransfer : 자바스크립트로 파일을 조작할 때 사용되는 인터페이스
        // - DataTransfer.items.add(): 파일추가
        // - DataTransfer.items.remove() : 파일제거
        // - DataTransfer.files : FileList 객체를 제공
        // -> <input type="file"> 요소에 파일을 동적으로 설정가능
        // --> input 태그의 files 속성은 FileList만 저장 가능하므로...

        dataTransfer.items.add(previousFile);
        // 이전 파일을 추가해두기: DataTransfer에 File 객체(여기서는 previousFile)를 추가
        inputImage.files = dataTransfer.files;
        // 이전 파일로 input 복구 : DataTransfer에 저장된 파일의 리스트를 FileList 객체로 반환
      }
    }
  } else {
    // 파일 선택이 취소된 경우 ( 첫시도에서는 change 이벤트가 일어나는게 아니라서 안됨. 이미지 하나 선택후 새로운 change가 일어나야 확인 가능)
    preview.src = previousPreview; // 이전 미리보기 이미지로 복원
    console.log("파일 선택 취소 : ", previousFile);

    // 파일 입력 복구: 이전 파일이 존재하면 다시 할당
    if (previousFile) {
      const dataTransfer = new DataTransfer();
      dataTransfer.items.add(previousFile);
      inputImage.files = dataTransfer.files; // 이전 파일로 input 복구
    }
  }
});

// 이미지 삭제 버튼 클릭 시
deleteImage.addEventListener("click", () => {
  inputImage.value = ""; // 파일 선택 초기화
  preview.src = ""; // 기본 이미지로 설정
  previousFile = null; // 이전 파일 초기화
  console.log(inputImage.files[0]);
});
