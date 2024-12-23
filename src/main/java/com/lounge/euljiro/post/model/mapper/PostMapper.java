package com.lounge.euljiro.post.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

	List<Map<String, Object>> selectPostTypeList();

}
