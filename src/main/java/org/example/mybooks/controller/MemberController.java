package org.example.mybooks.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.dto.JoinDto;
import org.example.mybooks.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("join") JoinDto join)
    {
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("join") JoinDto join, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "join";
        }
        if(!join.getPassword().equals(join.getConfirm_password())){
            bindingResult.rejectValue("confirm_password","passwordInconsistency","비밀번호가 맞지 않습니다.");
            return "join";
        }
    memberService.join(join);
    return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // src/main/resources/templates/member/login.html
    }
    @GetMapping("/logout")
    public String logout(){
        return "index";
    }

   }