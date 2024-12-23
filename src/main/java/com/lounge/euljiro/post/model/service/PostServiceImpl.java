package com.lounge.euljiro.post.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lounge.euljiro.post.model.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
	
	private final PostMapper mapper;
	
	@Override
	public List<Map<String, Object>> selectPostTypeList() {
		return mapper.selectPostTypeList();
	}
}
