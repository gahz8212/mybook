package org.example.mybooks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.constant.RoleType;
import org.example.mybooks.dto.JoinDto;
import org.example.mybooks.dto.MemberDto;
import org.example.mybooks.mapper.MemberMapper;
import org.example.mybooks.model.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void join(JoinDto dto){
        String encodePw=passwordEncoder.encode(dto.getPassword());
        Member member=Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encodePw).build();
        log.info("member:{},member.getId():{},getCode:{}",member,member.getId(), RoleType.USER.getCode());
        memberMapper.insertMember(member);
        log.info("DB 삽입 후 member.getId(): {}", member.getId());
        memberMapper.insertRoles(member.getId(), RoleType.USER.getCode());
    }
    public boolean checkPassword(Long id,String password){
        Member member=memberMapper.findById(id).orElseThrow();
        return passwordEncoder.matches(password,member.getPassword());
    }
    public void updatePassword(Long id,String password){
        log.info("id:"+id.toString()+"password:"+password);
        String encodePassword=passwordEncoder.encode(password);
        memberMapper.updatePassword(id,encodePassword);
    }

    public List<MemberDto> findAll() {
        return memberMapper.findAll();
    }

    public Member findByEmail(String email) {

        return memberMapper.findByEmail(email).orElseThrow();
    }
    public MemberDto findById(Long id){
        Member member=memberMapper.findById(id).orElseThrow();
            return MemberDto.from(member);

    }

    public void updateMember(MemberDto member) {
        memberMapper.deleteRole(member.getId());
        for(Integer roleCode:member.getRoleCodes()){
            memberMapper.insertRoles(member.getId(),roleCode);
        }
    }
    public String findNameByEmail(String email){
        return memberMapper.findNameByEmail(email);
    }
}
