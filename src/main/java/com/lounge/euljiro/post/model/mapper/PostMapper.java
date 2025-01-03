package com.lounge.euljiro.post.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.lounge.euljiro.post.model.dto.Post;

@Mapper
public interface PostMapper {

	List<Map<String, Object>> selectPostTypeList();

	int getListCount(int postCode);

	List<Post> selectPostList(Map<String, Integer> paramMap, RowBounds rowBounds);

	int deletePostLike(Map<String, Integer> map);

	int insertPostLike(Map<String, Integer> map);

	int selectLikeCount(Integer integer);

	int postInsert(Post post);

	Post postSelect(Post post);

	int postDelete(Post post);

	int updateReadCount(int postNo);

	int selectReadCount(int postNo);

}
