package com.lounge.euljiro.post.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lounge.euljiro.post.model.dto.Pagination;
import com.lounge.euljiro.post.model.dto.Post;
import com.lounge.euljiro.post.model.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostMapper mapper;

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
}
