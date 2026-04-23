package org.example.mybooks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.constant.RoleType;
import org.example.mybooks.dto.AuthorityDto;
import org.example.mybooks.dto.MemberDto;
import org.example.mybooks.dto.PasswordDto;
import org.example.mybooks.model.Member;
import org.example.mybooks.security.CustomUserDetails;
import org.example.mybooks.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name="Admin API",description = "사용자 권한 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AdminController {
    private final MemberService memberService;
    @Operation(summary="회원 리스트",description = "회원 정보를 보여줍니다.")
    @GetMapping("/admin")
    public ResponseEntity <List<MemberDto>> admin(){
        List<MemberDto> members=memberService.findAll();
        log.info("members:{}",members.toString());
        return ResponseEntity.ok(members);
//        model.addAttribute("members",members);
//        return "admin";
    }
//    @GetMapping("/admin/edit")
//    public String edit(Model model,@RequestParam("id")Long id){
//        MemberDto memberDto=memberService.findById(id);
//        model.addAttribute("member",memberDto);
//        model.addAttribute("allRoles", RoleType.values());
//        return "admin/edit";
//    }
@Operation(summary="회원 권한 업데이트",description = "회원의 권한을 업데이트 합니다.")
    @PostMapping("/admin/roles/update")
    public void editMember(@RequestBody AuthorityDto authorityDto) {
        log.info("수신 데이터: {}", authorityDto.getRoleArray());
        memberService.updateMemberRole(authorityDto);


    }



}
