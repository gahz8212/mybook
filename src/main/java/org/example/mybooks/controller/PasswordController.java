package org.example.mybooks.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.CodeIterator;
import org.example.mybooks.dto.PasswordDto;
import org.example.mybooks.security.CustomUserDetails;
import org.example.mybooks.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PasswordController {
    private final MemberService memberService;
    @GetMapping("/password")
    public String password(@ModelAttribute("password") PasswordDto password){
        return "password";
    }


    @PostMapping("/password")
    public String updatePassword(@Valid @ModelAttribute("password") PasswordDto password,
                                 BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails user){
        if(!memberService.checkPassword(user.getId(),password.getOld_password())){
            log.info(password.getOld_password());

            bindingResult.rejectValue("old_password","MissMatch","비밀번호가 잘못 되었습니다.");
        }
        if(!password.getNew_password().equals(password.getConfirm_password())){
            log.info(password.getNew_password(),password.getConfirm_password());
            bindingResult.rejectValue("confirm_password","MissMatch","비밀번호가 잘못 되었습니다.");
        }
        if(bindingResult.hasErrors()){
            return "/password";
        }
        memberService.updatePassword(user.getId(),password.getNew_password());
        return "redirect:/list";
    }
}
