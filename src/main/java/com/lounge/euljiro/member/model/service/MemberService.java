package com.lounge.euljiro.member.model.service;

public interface MemberService {

	/** 아이디 중복검사
	 * @param memberId
	 * @return 중복된 아이디가 포함된 행의 갯수(1 or 0)
	 */
	int checkDupId(String memberId);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return 중복된 닉네임이 포함된 행의 갯수(1 or 0)
	 */
	int checkDupNickname(String memberNickname);

}
