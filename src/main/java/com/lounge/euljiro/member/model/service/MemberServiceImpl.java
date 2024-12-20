package com.lounge.euljiro.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lounge.euljiro.member.model.mapper.MemberMapper;

@Transactional(rollbackFor = Exception.class)
@Service
public class MemberServiceImpl implements MemberService {

	// 등록된 Bean 중에서 같은 타입 or 상속관계인 Bean
	@Autowired // 의존성 주입(DI)
	private MemberMapper mapper;

	// Bcrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int checkDupId(String memberId) {
		return mapper.checkDupId(memberId);
	}
	
	@Override
	public int checkDupNickname(String memberNickname) {
		return mapper.checkDupNickname(memberNickname);
	}
}
