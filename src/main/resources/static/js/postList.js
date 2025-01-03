// 글쓰기 버튼
const insertBtn = document.querySelector("#insertBtn");

if (insertBtn) {
	insertBtn.addEventListener("click", () => {
		location.href = `/post/${postCode}/insert`;
	});
}

