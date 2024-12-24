package com.lounge.euljiro.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@SessionAttributes({"loginMember"})
@Controller
public class MainController {
	
	@RequestMapping("/") //   "/" 요청 매핑 
	public String mainPage(HttpSession session) {
		
		// 접두사/접미사 제외
		// classpath:/templates/
		// .html
		return "lounge/main";
	}

	// LoginFilter -> loginError 리다이렉트
	// -> message 만들어서 메인페이지로 리다이렉트
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용해 주세요~");
		return "redirect:/member/login";
	}
	
	
	
}
