package com.sparta.springauth.controller;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @RestController 가 아니기 때문에   return에 있는 코드로 templates 밑에서 페이지를 찾아준다 .!! 복습!!
    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }
    // 회원가입 성공하면 로그인 하라고 로그인페이지로 이동해야하기 때문에 String 값 반환
    @PostMapping("/user/signup")
    public String signup(SignupRequestDto requestDto){
        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    @PostMapping("/user/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res) {
        //오류가 발생했다면 로그인 페이지에 에러 표시를 해줘야한다.
        try {
            userService.login(requestDto, res);
        } catch (Exception e) {
            // 이렇게 보내달라고 클라이언트에서 요청이 왔다. error를 보내준다.
            return "redirect:/api/user/login-page?error";
        }

        return "redirect:/";
    }
}