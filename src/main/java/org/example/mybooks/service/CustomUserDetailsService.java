package org.example.mybooks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.constant.RoleType;
import org.example.mybooks.mapper.MemberMapper;
import org.example.mybooks.model.Member;
import org.example.mybooks.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
       log.info("email:"+ email);
        Member member=memberMapper.findByEmail(email).orElseThrow();
        if(member==null) {
            throw new UsernameNotFoundException("User not found");
        }
        ////
        List<GrantedAuthority> authorities =member.getRoleCodes().stream()
                .map(code->new SimpleGrantedAuthority("ROLE_"+ RoleType.fromCode(code).name()))
                .collect(Collectors.toList());
        ////
//        return new CustomUserDetails(member);
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) // DB에 저장된 암호화된 비번
                .authorities(authorities) // 권한 설정
                .build();

    }

}
