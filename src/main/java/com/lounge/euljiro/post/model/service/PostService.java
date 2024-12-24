package com.lounge.euljiro.post.model.service;

import java.util.List;
import java.util.Map;

public interface PostService {

	List<Map<String, Object>> selectPostTypeList();

	Map<String, Object> selectPostList(Map<String, Integer> paramMap);

	int postLike(Map<String, Integer> map);

}
