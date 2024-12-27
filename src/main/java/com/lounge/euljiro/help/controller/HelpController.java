package com.lounge.euljiro.help.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lounge.euljiro.post.controller.PostController;
import com.lounge.euljiro.post.model.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("help")
@Slf4j
@RequiredArgsConstructor
public class HelpController {
	
	
	@GetMapping("center")
	public String helpCenter() {
		return "help/main";
	}

}