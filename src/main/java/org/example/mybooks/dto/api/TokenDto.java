package org.example.mybooks.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TokenDto {
    private String grantType;    // 보통 "Bearer"로 고정
    private String accessToken;  // 실제 인증에 쓰이는 짧은 토큰
    private String userName;
    private List<String> role;
//    private String refreshToken; // 재발급에 쓰이는 긴 토큰

//    private Long accessTokenExpiresIn; // (선택) 만료 시간 정보 (ms)
}