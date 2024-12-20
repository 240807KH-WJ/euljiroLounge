package com.lounge.euljiro.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

	/** 아이디 중복검사
	 * @param memberId
	 * @return
	 */
	int checkDupId(String memberId);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return
	 */
	int checkDupNickname(String memberNickname);

}
