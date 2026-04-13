package org.example.mybooks.service;

import lombok.RequiredArgsConstructor;
import org.example.mybooks.dto.api.TokenDto;
import org.example.mybooks.dto.api.TokenRequestDto;
import org.example.mybooks.security.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisTemplate<String, String> redisTemplate; // 여기에 final이 꼭 붙어야 합니다.
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token에서 유저 정보 추출 (만료된 토큰이라도 Claims는 꺼낼 수 있음)
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. Redis에서 해당 유저의 Refresh Token 가져오기
        String refreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // 4. 일치 여부 확인
        if (refreshToken == null || !refreshToken.equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰 정보가 일치하지 않거나 이미 로그아웃된 사용자입니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateToken(authentication);

        // 6. Redis에 새로운 Refresh Token 업데이트 (기존 TTL 유지 또는 새로 설정)
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                tokenDto.getRefreshToken(),
                7, TimeUnit.DAYS
        );

        return tokenDto;
    }
}
