package com.lounge.euljiro.post.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.lounge.euljiro.post.model.dto.Post;

public interface PostService {

	List<Map<String, Object>> selectPostTypeList();

	Map<String, Object> selectPostList(Map<String, Integer> paramMap);

	int postLike(Map<String, Integer> map);

	int postInsert(Post post);

	/** 문의글작성 + 이미지
	 * @param post
	 * @param qnaImg
	 * @return
	 */
	int postInsert(Post post, MultipartFile qnaImg) throws IllegalStateException, IOException;
 
	/** 게시글 조회
	 * @param post
	 * @return
	 */
	Post postSelect(Post post);
 
	/** 게시글 삭제
	 * @param post
	 * @return
	 */
	int postDelete(Post post);

	/** 조회수증가
	 * @param postNo
	 * @return
	 */
	int updateReadCount(int postNo);

}
