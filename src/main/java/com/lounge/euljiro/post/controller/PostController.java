package com.lounge.euljiro.post.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.lounge.euljiro.member.model.dto.Member;
import com.lounge.euljiro.post.model.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("post")
@Slf4j
@RequiredArgsConstructor
public class PostController {
	
	private final PostService service;
	
	
	/** 게시판 목록조회(심리상담, 마인드포스팃, 문의게시판)
	 * @param postCode
	 * @param cp
	 * @param model
	 * @return
	 */
	@GetMapping("{postCode:[1-3]}")
	public String selectPostList(@PathVariable("postCode") int postCode,
								@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, 
								@SessionAttribute(value="loginMember", required = false) Member loginMember,
								Model model) {

		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;
		
		// 전달용 파라미터 묶음 map
		Map<String, Integer> paramMap = new HashMap<>();
		
		paramMap.put("postCode", postCode);
		paramMap.put("cp", cp);
		
		// 로그인 상태인 경우에만 memberNo 추가
		if(loginMember != null) {
			paramMap.put("memberNo", loginMember.getMemberNo());
		}
				
		// 게시글 목록 조회 서비스 호출
		map = service.selectPostList(paramMap);
		
		// model에 반환 받은 값을 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("postList", map.get("postList"));
		
		if(postCode == 2) {
			return "post/mindPostit";
		}
		
		return "post/postList";		// forward : post/postList.html
	}

	
	/** 게시글 좋아요 체크/해제(비동기)
	 * @return
	 */
	@ResponseBody 
	@PostMapping("like")
	public int postLike(@RequestBody Map<String, Integer> map) {
		return service.postLike(map);
	}
}
