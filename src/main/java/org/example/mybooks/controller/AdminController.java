package org.example.mybooks.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.constant.RoleType;
import org.example.mybooks.dto.MemberDto;
import org.example.mybooks.dto.PasswordDto;
import org.example.mybooks.model.Member;
import org.example.mybooks.security.CustomUserDetails;
import org.example.mybooks.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final MemberService memberService;

    @GetMapping("/admin")
    public String admin(Model model){
        List<MemberDto> members=memberService.findAll();
//        log.info(members.toString());
        model.addAttribute("members",members);
        return "admin";
    }
    @GetMapping("/admin/edit")
    public String edit(Model model,@RequestParam("id")Long id){
        MemberDto memberDto=memberService.findById(id);
        model.addAttribute("member",memberDto);
        model.addAttribute("allRoles", RoleType.values());
        return "admin/edit";
    }
    @PostMapping("/admin/edit")
    public String editMember(@ModelAttribute("member") MemberDto member) {
        log.info("수신 데이터: {}", member.toString());
        memberService.updateMember(member);
        return "redirect:/list";
    }



}
