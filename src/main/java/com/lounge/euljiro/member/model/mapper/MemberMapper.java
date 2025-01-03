package com.lounge.euljiro.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.lounge.euljiro.member.model.dto.Member;

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

	/** 회원가입
	 * @param inputMember
	 * @return
	 */
	int register(Member inputMember);

	/** 로그인
	 * @param memberId
	 * @return
	 */
	Member login(String memberId);

}
