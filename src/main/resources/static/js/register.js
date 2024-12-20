// 다음 주소 API
function execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

      // 각 주소의 노출 규칙에 따라 주소를 조합한다.
      // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
      var addr = ""; // 주소 변수

      //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
      if (data.userSelectedType === "R") {
        // 사용자가 도로명 주소를 선택했을 경우
        addr = data.roadAddress;
      } else {
        // 사용자가 지번 주소를 선택했을 경우(J)
        addr = data.jibunAddress;
      }

      // 우편번호와 주소 정보를 해당 필드에 넣는다.
      document.getElementById("postcode").value = data.zonecode;
      document.getElementById("address").value = addr;
      // 커서를 상세주소 필드로 이동한다.
      document.getElementById("detailAddress").focus();
    },
  }).open();
}

// 주소 검색 버튼 클릭 시
document
  .querySelector("#searchAddress")
  .addEventListener("click", execDaumPostcode);

// ----------------------------------------------
// **** 회원 가입 유효성 검사 *****

// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 객체
// - true  == 해당 항목은 유효한 형식으로 작성됨
// - false == 해당 항목은 유효하지 않은 형식으로 작성됨
const checkObj = {
  regMemberId : false,
  dupMemberId: false,
  regMemberNickname : false,
  dupMemberNickname: false,
  memberPw: false,
  memberPwConfirm: false,
};

// 아이디 중복검사
// 1) 아이디 유효성 검사에 사용될 요소 얻어오기
const memberId = document.querySelector("#memberId");
const memberIdMessage = document.querySelector("#memberId-message");

// 2) 아이디가 입력(input) 될 때 마다 유효성 검사 수행
memberId.addEventListener("input", (e) => {
  // 작성된 아이디 값 얻어오기
  const inputId = e.target.value;

  // 3) 입력된 아이디가 없을 경우
  if (inputId.trim().length === 0) {
    memberIdMessage.innerText = "아이디를 입력해주세요.";

    // 이메일 유효성 검사 여부를 false 변경
    checkObj.regMemberId = false;

    // 잘못 입력한 띄어쓰기가 있을 경우 없앰
    memberId.value = "";

    return;
  }

  // 4) 입력된 아이디가 있을 경우 정규식 검사
  //    (알맞은 형태로 작성했는지 검사)
  // 영어대소문자 5~15글자
  const regExp = /^[a-zA-Z]{5,15}$/;

  // 입력 받은 아이디가 정규식과 일치하지 않는 경우
  // (알맞은 아이디 형태가 아닌 경우)
  if (!regExp.test(inputId)) {
    memberIdMessage.innerText = "알맞은 아이디 형식으로 작성해주세요.";
    memberIdMessage.classList.add("error"); // 글자를 빨간색으로 변경
    memberIdMessage.classList.remove("confirm"); // 초록색 제거
    checkObj.regMemberId = false; // 유효하지 않은 이메일임을 기록

  } else {
    memberIdMessage.innerText = "";
    memberIdMessage.classList.add("confirm"); // 글자를 초록색으로 변경
    memberIdMessage.classList.remove("error"); // 빨간색 제거
    checkObj.regMemberId = true; // 유효하지 않은 이메일임을 기록
  }

});

// 이메일 중복검사
function checkDupID() {

  if(!checkObj.regMemberId) {
    alert("아이디가 유효하지 않습니다.");
    return;
  }

  // 5) 유효한 아이디 형식인 경우 중복 검사 수행
  // 비동기(ajax)
  fetch("/member/checkDupId?memberId=" + memberId.value)
    .then((resp) => resp.text())
    .then((count) => {
      // count : 1이면 중복, 0이면 중복 아님
      // ==   :  값이 같은지     ex) "1" == 1  -> true
      // ===  : 타입까지 같은지  ex) "1" === 1 -> false
      if (count == 1) {
        // 중복 O
        memberIdMessage.innerText = "이미 사용중인 이메일 입니다.";
        memberIdMessage.classList.add("error");
        memberIdMessage.classList.remove("confirm");
        checkObj.dupMemberId = false; // 중복은 유효하지 않은 상태다..
      } else {
        // 중복 X 경우
        memberIdMessage.innerText = "사용 가능한 아이디입니다.";
        memberIdMessage.classList.add("confirm");
        memberIdMessage.classList.remove("error");
        checkObj.dupMemberId = true; // 유효한 이메일
      }

    })
    .catch((error) => {
      // fetch 수행 중 예외 발생 시 처리
      console.log(error); // 발생한 예외 출력
    });

}


// 닉네임 중복검사
// 1) 닉네임 유효성 검사에 사용될 요소 얻어오기
const memberNickname = document.querySelector("#memberNickname");
const memberNicknameMessage = document.querySelector("#memberNickname-message");

// 2) 닉네임이 입력(input) 될 때 마다 유효성 검사 수행
memberNickname.addEventListener("input", e => {
  // 작성된 아이디 값 얻어오기
  const inputNickname = e.target.value;

  // 3) 입력된 아이디가 없을 경우
  if (inputNickname.trim().length === 0) {
    memberNicknameMessage.innerText = "닉네임을 입력해주세요.";

    // 이메일 유효성 검사 여부를 false 변경
    checkObj.regMemberNickname = false;

    // 잘못 입력한 띄어쓰기가 있을 경우 없앰
    memberNickname.value = "";

    return;
  }

  // 4) 입력된 닉네임이 있을 경우 정규식 검사
  //    (알맞은 형태로 작성했는지 검사)
  // 한글 2~10글자
  const regExp = /^[가-힣]{2,10}$/;

  // 입력 받은 닉네임이 정규식과 일치하지 않는 경우
  // (알맞은 닉네임 형태가 아닌 경우)
  if (!regExp.test(inputNickname)) {
    memberNicknameMessage.innerText = "알맞은 닉네임 형식으로 작성해주세요.";
    memberNicknameMessage.classList.add("error"); // 글자를 빨간색으로 변경
    memberNicknameMessage.classList.remove("confirm"); // 초록색 제거
    checkObj.regMemberNickname = false; // 유효하지 않은 이메일임을 기록
    return;
  } 

  // 5) 유효한 닉네임 형식인 경우 중복 검사 수행
  // 비동기(ajax)
  fetch("/member/checkDupNickname?memberNickname=" + inputNickname)
    .then((resp) => resp.text())
    .then((count) => {
      // count : 1이면 중복, 0이면 중복 아님
      // ==   :  값이 같은지     ex) "1" == 1  -> true
      // ===  : 타입까지 같은지  ex) "1" === 1 -> false
      if (count == 1) {
        // 중복 O
        memberNicknameMessage.innerText = "이미 사용중인 닉네임 입니다.";
        memberNicknameMessage.classList.add("error");
        memberNicknameMessage.classList.remove("confirm");
        checkObj.dupMemberId = false; // 중복은 유효하지 않은 상태다..
      } else {
        // 중복 X 경우
        memberNicknameMessage.innerText = "사용 가능한 닉네임입니다.";
        memberNicknameMessage.classList.add("confirm");
        memberNicknameMessage.classList.remove("error");
        checkObj.dupMemberId = true; // 유효한 이메일
      }

    })
    .catch((error) => {
      // fetch 수행 중 예외 발생 시 처리
      console.log(error); // 발생한 예외 출력
    });
});

// 비밀번호 유효성검사
// 비밀번호 체크
// 주소 검사
// 모두 완료되면 회원가입 가능하도록 구현