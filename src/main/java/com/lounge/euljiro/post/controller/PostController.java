package com.lounge.euljiro.post.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public String postInsert(Post post, @PathVariable("postCode") int postCode,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "img", required = false) MultipartFile qnaImg, RedirectAttributes ra)
			throws IllegalStateException, IOException {

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

		return result > 0 ? "redirect:/post/" + postCode // 게시글 목록 조회
				: "redirect:/post/" + postCode + "/insert"; // 게시글 작성
	}

	/**
	 * 포스트잇 글작성
	 * 
	 * @param post
	 * @return
	 */
	@ResponseBody
	@PostMapping("2/insert")
	public int postInsert(@RequestBody Post post) {

		return service.postInsert(post);

	}

	/**
	 * 게시글(심리상담/문의게시판) 상세 조회 상세 조회 요청 주소 /post/1/2001?cp=1
	 * 
	 * @param postCode
	 * @param postNo
	 * @param loginMember
	 * @return
	 */
	@GetMapping("{postCode:[1,3]}/{postNo:[0-9]+}")
	public String postSelect(@PathVariable("postCode") int postCode, @PathVariable("postNo") int postNo, Model model,
			RedirectAttributes ra, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
			HttpServletRequest req, // 요청에 담긴 쿠키 얻어오기
			HttpServletResponse resp // 새로운 쿠키 만들어서 응답하기 
			) {

		// 게시글 상세 조회 서비스 호출

		// 1) Post DTO로 전달할 파라미터 묶기
		Post post = Post.builder().postCode(postCode).postNo(postNo).build();


		// 로그인 상태인 경우에만 memberNo 추가
		if (loginMember != null) {
			post.setMemberNo(loginMember.getMemberNo());
		}

		// 2) 서비스 호출
		Post selectPost = service.postSelect(post);

		log.debug("조회된 Post : " + selectPost);

		String path = null;

		// 조회 결과가 없는 경우
		if (selectPost == null) {
			path = "redirect:/post/" + postCode; // 목록 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다");

		} else {
			/* --------------- 쿠키를 이용한 조회 수 증가 ------------------------- */

			// 비회원 또는 로그인한 회원의 글이 아닌 경우 ( == 글쓴이를 뺀 다른 사람)
			if (loginMember == null || loginMember.getMemberNo() != selectPost.getMemberNo()) {

				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();

				Cookie c = null;

				for (Cookie temp : cookies) {

					// 요청에 담긴 쿠키에 "readpostNo" 가 존재 할 때
					if (temp.getName().equals("readpostNo")) {
						c = temp;
						break;
					}

				}

				int result = 0; // 조회수 증가 결과를 저장할 변수

				// "readpostNo"가 쿠키에 없을 때
				if (c == null) {

					// 새 쿠키 생성("readpostNo", [게시글번호])
					c = new Cookie("readpostNo", "[" + postNo + "]");
					result = service.updateReadCount(postNo);

				} else {
					// "readpostNo"가 쿠키에 있을 때
					// "readpostNo" : [2][30][400][2000][4000]

					// 현재 글을 처음 읽는 경우
					if (c.getValue().indexOf("[" + postNo + "]") == -1) {

						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "[" + postNo + "]");
						// [2][30][400][2000][4000][2003]
						result = service.updateReadCount(postNo);
					}

				}

				// 조회 수 증가 성공 / 조회 성공 시
				if (result > 0) {

					// 먼저 조회된 Post의 readCount 값을
					// result 값을 다시 세팅
					selectPost.setReadCount(result);

					// 쿠키 적용 경로 설정
					c.setPath("/"); // "/" 이하 경로 요청 시 쿠키 서버로 전달

					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();

					// 다음날 자정 지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

					// 다음날 자정까지 남은 시간 계산 (초 단위)
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();

					// 쿠키 수명 설정
					c.setMaxAge((int) secondsUntilNextDay);

					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
				}

			}

			/*
			 * --------------------------- 쿠키를 이용한 조회 수 증가 끝 -----------------------------
			 */

			// 조회 결과가 있는 경우
			path = "post/postDetail"; // PostDetail.html 로 forward

			// Post - 게시글 일반 내용 + imageList + commentList
			model.addAttribute("post", selectPost);

		}

		return path;
	}

	/**
	 * 포스트잇 상세 조회
	 * 
	 * @param postCode
	 * @param postNo
	 * @return
	 */
	@ResponseBody
	@GetMapping("{postCode:[2]}/{postNo:[0-9]+}")
	public Post postitSelect(@PathVariable("postCode") int postCode, @PathVariable("postNo") int postNo,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {

		Post post = Post.builder().postCode(postCode).postNo(postNo).build();

		if (postCode == 2 && loginMember != null) {
			post.setMemberNo(loginMember.getMemberNo());
		}

		return service.postSelect(post);
	}

	/**
	 * 게시글 삭제
	 * 
	 * @param postCode
	 * @param postNo
	 * @param loginMember
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("{postCode:[1-3]}/{postNo:[0-9]+}")
	public int postitDelete(@PathVariable("postCode") int postCode, @PathVariable("postNo") int postNo) {

		Post post = Post.builder().postCode(postCode).postNo(postNo).build();

		return service.postDelete(post);

	}
}
