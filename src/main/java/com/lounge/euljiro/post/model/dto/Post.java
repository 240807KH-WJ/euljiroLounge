package com.lounge.euljiro.post.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Post {

	// POST 테이블 컬럼
	private int postNo;
	private String postTitle;
	private String postContent;
	private String postCreateDate;
	private int readCount;
	private String postDelFl;
	private String qnaImg;
	private int postCode;
	private int memberNo;

	// MEMBER 테이블 조인
	private String memberNickname;
	
	// POST_LIKE 테이블 조인
	private int postLikeCheck; 

}
