package com.lounge.euljiro.post.controller;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lounge.euljiro.member.model.dto.Member;
import com.lounge.euljiro.post.model.dto.Post;
import com.lounge.euljiro.post.model.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("post")
@Slf4j
@RequiredArgsConstructor
public class PostController {

	private final PostService service;

	/**
	 * 게시판 목록조회(심리상담, 마인드포스팃, 문의게시판)
	 * 
	 * @param postCode
	 * @param cp
	 * @param model
	 * @return
	 */
	@GetMapping("{postCode:[1-3]}")
	public String selectPostList(@PathVariable("postCode") int postCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {

		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;

		// 전달용 파라미터 묶음 map
		Map<String, Integer> paramMap = new HashMap<>();

		paramMap.put("postCode", postCode);
		paramMap.put("cp", cp);

		// 로그인 상태인 경우에만 memberNo 추가
		if (loginMember != null) {
			paramMap.put("memberNo", loginMember.getMemberNo());
		}

		// 게시글 목록 조회 서비스 호출
		map = service.selectPostList(paramMap);

		// model에 반환 받은 값을 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("postList", map.get("postList"));

		if (postCode == 2) {
			return "post/mindPostit";
		}

		return "post/postList"; // forward : post/postList.html
	}

	/**
	 * 게시글 좋아요 체크/해제(비동기)
	 * 
	 * @return
	 */
	@ResponseBody
	@PostMapping("like")
	public int postLike(@RequestBody Map<String, Integer> map) {
		return service.postLike(map);
	}

	/**
	 * 게시글 작성 페이지 이동
	 * 
	 * @return
	 */
	@GetMapping("{postCode:[1-3]}/insert")
	public String postInsert(@PathVariable("postCode") int postCode) {
		return "post/insert";
	}

	/**
	 * 게시글 작성(INSERT) - 심리상담/문의게시판
	 * 
	 * @return
	 */
	@PostMapping("{postCode:[1-3]}/insert")
	public String postInsert(Post post, 
			@PathVariable("postCode") int postCode,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "img", required = false) MultipartFile qnaImg, 
			RedirectAttributes ra) throws IllegalStateException, IOException {

		// 현재 로그인한 회원의 번호를 post 에 세팅
		post.setMemberNo(loginMember.getMemberNo());
		
		int result = 0;
		
		// 문의글(postCode == 3)이면서, 이미지가 있는 경우 이미지 처리
	    if (postCode == 3 && qnaImg != null && !qnaImg.isEmpty()) {
	        result = service.postInsert(post, qnaImg); // 이미지 포함 서비스 호출
	    
	    } else { // if문 조건중 하나라도 false 인 경우
	        result = service.postInsert(post); // 일반 서비스 호출
	    }

	    // 결과에 따른 메시지 설정 및 리다이렉트
	    String message = (result > 0) ? "게시글이 등록되었습니다" : "게시글 등록 실패";
	    ra.addFlashAttribute("message", message);

	    return result > 0 
	        ? "redirect:/post/" + postCode  // 게시글 목록 조회
	        : "redirect:/post/" + postCode + "/insert"; // 게시글 작성
	}
	
	
	/** 포스트잇 글작성
	 * @param post
	 * @return
	 */
	@ResponseBody
	@PostMapping("2/insert") 
	public int postInsert(@RequestBody Post post) {
		
		return service.postInsert(post);
		
	}
	
	
	/** 게시글 조회
	 * @param postCode
	 * @param postNo
	 * @return
	 */
	@ResponseBody
	@GetMapping("{postCode:[1-3]}/{postNo:[0-9]+}")
	public Post postitSelect(@PathVariable("postCode") int postCode, 
							@PathVariable("postNo") int postNo, 
							@SessionAttribute(value = "loginMember", required = false) Member loginMember
							) {
		
		Post post = Post.builder()
					.postCode(postCode)
					.postNo(postNo)
					.build();
		
		if(postCode == 2 && loginMember != null) {
			post.setMemberNo(loginMember.getMemberNo());
		}
		
		return service.postSelect(post);
	}
}
