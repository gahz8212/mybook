package org.example.mybooks.security;

import org.example.mybooks.constant.RoleType;
import org.example.mybooks.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails  implements UserDetails {
    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }
    public Long getId(){
        return member.getId();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {


        return member.getRoleCodes()
                .stream()
                .map(code->new SimpleGrantedAuthority(RoleType.fromCode(code).getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public  String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
    }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

}
