package com.lounge.euljiro.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lounge.euljiro.member.model.dto.Member;
import com.lounge.euljiro.member.model.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {

	@Autowired // 의존성 주입(DI)
	private MemberService service;
	
	// 로그인 페이지 이동
	@GetMapping("login")
	public String login() {
		return "common/login";
	}
	
	/** 로그인 
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략)
	 * 						memberEmail, memberPw 세팅된 상태
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체(request -> session -> request)
	 * @param model : 데이터 전달용 객체 (기본 request scope) 
	 * 				/ (@SessionAttributes 어노테이션과 함께 사용시 session scope 이동)
	 * @param saveId
	 * @param resp
	 * @return
	 */
	@PostMapping("login")
	public String login(/*@ModelAttribute*/ Member inputMember, 
						RedirectAttributes ra,
						Model model,
						@RequestParam(value="saveId", required = false) String saveId,
						HttpServletResponse resp ) throws Exception {
		
		// 체크박스
		// - 체크가    된 경우 : "on"
		// - 체크가  안된 경우 : null
		
		// 로그인 서비스 호출
		String path = null;
		
		try {
			Member loginMember = service.login(inputMember);
			
			// 로그인 실패 시
			if(loginMember == null) {
				path = "/member/login";
				ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			} else {
				
				// Session scope에 loginMember 추가
				model.addAttribute("loginMember", loginMember);
				// 1단계 : request scope 에 세팅됨
				// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션 작성하여
				//		   session scope로 이동
				
				// **********Cookie************
				
				// 이메일 저장 
				
				// 쿠키 객체 생성 (K:V)
				Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
				// saveId=user01@kh.or.kr
				
				// 쿠키가 적용될 경로 설정
				// -> 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될지 지정
				
				// ex)  "/"  :  IP 또는 도메인 또는 localhost
				//			   --> 메인페이지 + 그 하위 주소 모두 
				cookie.setPath("/");
				
				// 쿠키의 만료 기간 지정
				if(saveId != null) { // 아이디 저장 체크 시
					cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 (초 단위로 지정)
					
				} else { // 미체크 시 
					cookie.setMaxAge(0); // 0초 (클라이언트 쿠키 삭제)
				}
				
				// 응답 객체에 쿠키 추가 -> 클라이언트 전달
				resp.addCookie(cookie);
				
				path = "/";
				
			}
			
		} catch (Exception e) {
			log.info("로그인 중 예외 발생 try-catch로 예외처리");
			e.printStackTrace();
		}
		
		
		return "redirect:" + path; // 메인페이지로 redirect
	}
	
	/** 로그아웃 : session에 저장된 로그인된 회원 정보를 없앰
	 * @param SessionStatus : @SessionAttributes로 지정된 특정 속성을 세션에서 제거 기능 제공 객체 
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료 시킴 (== 세션에서 @SessionAttributes로 등록된 세션 제거)
		
		return "redirect:/";
	}
	

	// 회원가입 페이지 이동
	@GetMapping("register")
	public String register() {
		return "member/register";
	}

	// 아이디 중복검사 (DB에 중복되는 ID가 존재하는지 검사) - 비동기요청
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkDupId")
	public int checkDupId(@RequestParam("memberId") String memberId) {

		// log.debug("memberId : " + memberId);

		return service.checkDupId(memberId);
	}

	// 닉네임 중복검사 (DB에 중복되는 ID가 존재하는지 검사) - 비동기요청
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkDupNickname")
	public int checkDupNickname(@RequestParam("memberNickname") String memberNickname) {

		return service.checkDupNickname(memberNickname);
	}

	// 회원가입
	@PostMapping("register")
	public String register(Member inputMember, 
			@RequestParam("memberAddress") String[] memberAddress,
			RedirectAttributes ra) {
		log.debug("inputMember : " + inputMember);

		// 회원가입 서비스 호출
		int result = service.register(inputMember, memberAddress);

		String path = null;
		String message = null;

		if (result > 0) { // 성공 시
			message = inputMember.getMemberNickname() + "님의 가입을 환영 합니다!";
			path = "/";

		} else { // 실패
			message = "회원 가입 실패...";
			path = "signup";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
		// 성공 -> redirect:/
		// 실패 -> redirect:signup (상대경로)
		// 현재 주소 /member/signup (GET 방식 요청)
	}

	
	
}
