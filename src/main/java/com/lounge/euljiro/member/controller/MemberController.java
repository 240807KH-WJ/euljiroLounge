package com.lounge.euljiro.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lounge.euljiro.member.model.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {
	
	@Autowired // 의존성 주입(DI)
	private MemberService service;
	
	@GetMapping("register")
	public String register() {
		return "member/register";
	}
	
	// 아이디 중복검사 (DB에 중복되는 ID가 존재하는지 검사) - 비동기요청
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkDupId")
	public int checkDupId(@RequestParam("memberId") String memberId) {
	
		//log.debug("memberId : " + memberId);
		
		return service.checkDupId(memberId);
	}
	
	// 닉네임 중복검사 (DB에 중복되는 ID가 존재하는지 검사) - 비동기요청
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkDupNickname")
	public int checkDupNickname(@RequestParam("memberNickname") String memberNickname) {
		
		return service.checkDupNickname(memberNickname);
	}
	
}
