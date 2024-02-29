package com.example.springbootdeveloper.controller;

import com.example.springbootdeveloper.dto.AddUserRequest;
import com.example.springbootdeveloper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login"; // 회원가입 완료 후에 로그인 페이지로 리다이렉트
    }
}