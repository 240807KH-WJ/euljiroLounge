package com.lounge.euljiro.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//DTO (Data Transfer Object)
//- 데이터 전달용 객체
//- DB에 조회된 결과 또는 SQL 구문에 사용할 값을 전달하는 용도
//- 관련성 있는 데이터를 한번에 묶어서 다룸

@Getter						// getter
@Setter						// setter
@ToString					// toString
@NoArgsConstructor			// 기본생성자
@AllArgsConstructor			// 모든필드초기화용 매개변수생성자
@Builder					// builder 패턴이용하여 객체 생성 가능
public class Member {
	private int memberNo;				// 회원고유식별번호
	private String memberId;			// 회원아이디
	private String memberPw;			// 회원비밀번호
	private String memberNickname;		// 회원닉네임
	private String memberAddress;		// 회원주소
	private String enrollDate;			// 가입일
	private String memberDelFl;			// 회원탈퇴여부(Y/N)
	private int authority;				// 권한(1:일반,2:관리자)
}
