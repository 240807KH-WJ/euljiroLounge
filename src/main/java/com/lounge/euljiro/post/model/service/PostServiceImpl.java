package com.lounge.euljiro.post.model.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lounge.euljiro.common.util.Utility;
import com.lounge.euljiro.post.model.dto.Pagination;
import com.lounge.euljiro.post.model.dto.Post;
import com.lounge.euljiro.post.model.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties")
public class PostServiceImpl implements PostService {

	private final PostMapper mapper;

	@Value("${my.qna.web-path}")
	private String qnaWebPath; // /images/qna/

	@Value("${my.qna.folder-path}")
	private String qnaFolderPath; // C:/wj-uploadFiles/qna/

	@Override
	public List<Map<String, Object>> selectPostTypeList() {
		return mapper.selectPostTypeList();
	}

	@Override
	public Map<String, Object> selectPostList(Map<String, Integer> paramMap) {

		int postCode = paramMap.get("postCode");
		int cp = paramMap.get("cp");
		// 1. 지정된 게시판(postCode)에서
		// 삭제되지 않은 게시글 수를 조회
		int listCount = mapper.getListCount(postCode);

		// 2. 1번의 결과 + cp 를 이용해서
		// Pagination 객체를 생성
		// * Pagination 객체 : 게시글 목록 구성에 필요한 값을 저장한 객체
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		/*
		 * ROWBOUNDS 객체 (MyBatis 제공 객체) - 지정된 크기 만큼 건너뛰고 (offset) 제한된 크기만큼(limit)의 행을
		 * 조회하는 객체
		 * 
		 * --> 페이징 처리가 굉장히 간단해짐
		 * 
		 */
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		// Mapper 메서드 호출 시 원래 전달 할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// RowBounds를 이용할 때.
		// -> 첫 번째 매개변수 -> SQL에 전달할 파라미터
		// -> 두 번째 매개변수 -> RowBounds 객체 전달
		List<Post> postList = mapper.selectPostList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("postList", postList);

		// 5. 결과 반환
		return map;
	}

	@Override
	public int postLike(Map<String, Integer> map) {
		int result = 0;

		// 1. 좋아요가 체크된 상태인 경우(likeCheck == 1)
		// -> BOARD_LIKE 테이블에 DELETE
		if (map.get("postLikeCheck") == 1) {

			result = mapper.deletePostLike(map);

		} else {
			// 2. 좋아요가 해제된 상태인 경우(likeCheck == 0)
			// -> BOARD_LIKE 테이블에 INSERT

			result = mapper.insertPostLike(map);

		}

		// 다시 해당 게시글의 좋아요 개수 조회해서 반환
		if (result > 0) {
			return mapper.selectLikeCount(map.get("postNo"));
		}

		return -1;
	}

	@Override
	public int postInsert(Post post) {
		return mapper.postInsert(post);
	}

	// 문의글 작성 + 이미지
	@Override
	public int postInsert(Post post, MultipartFile qnaImg) throws IllegalStateException, IOException {
		
		// 이미지 경로
		String imgPath = null;

		// 변경명 저장
		String rename = null;

		// 업로드한 이미지가 있을 경우
		// - 이미지 경로 조합 (클라이언트 접근 경로+리네임파일명)
		if (!qnaImg.isEmpty()) {
			// updatePath 조합

			// 1. 파일명 변경
			rename = Utility.fileRename(qnaImg.getOriginalFilename());

			// 2. /myPage/profile/변경된파일명
			imgPath = qnaWebPath + rename;
		}

		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		post.setQnaImg(imgPath);

		// UPDATE 수행
		int result = mapper.postInsert(post);

		if (result > 0) { // DB에 수정 성공 시

			// 이미지를 없앤 경우(NULL로 수정한 경우)를 제외
			// -> 업로드한 이미지가 있을 경우
			if (!qnaImg.isEmpty()) {
				// 파일을 서버 지정된 폴더에 저장
				qnaImg.transferTo(new File(qnaFolderPath + rename));
			}
		}

		return result;
	}


	// 게시글 조회
	@Override
	public Post postSelect(Post post) {
		return mapper.postSelect(post);
	}
}
