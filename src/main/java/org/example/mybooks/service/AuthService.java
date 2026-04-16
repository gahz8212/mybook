package org.example.mybooks.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.dto.api.LoginRequestDto;
import org.example.mybooks.dto.api.TokenDto;
import org.example.mybooks.security.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;
    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // 1. Login ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행될 때 CustomUserDetailsService의 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String userName=memberService.findNameByEmail((loginRequestDto.getEmail()));
        // 3. 인증 정보를 기반으로 JWT 토큰(Access + Refresh) 생성
        // 아까 만든 createTokenDto를 여기서 호출합니다.
        TokenDto tokenDto = tokenProvider.generateToken(authentication,userName);
        String refreshToken= tokenProvider.refreshToken(loginRequestDto.getEmail());
        log.info("refreshToken:{}",refreshToken);


        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                refreshToken,
                7,TimeUnit.DAYS
        );
        // 3. [핵심] RefreshToken을 쿠키에 주입
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)    // JavaScript에서 접근 불가 (보안 핵심)
                .secure(false)      // HTTPS 환경에서만 전송 (운영 환경 필수)
                .path("/")         // 모든 경로에서 쿠키 유효
                .maxAge(7 * 24 * 60 * 60) // 7일 (초 단위)
                .sameSite("Lax")   // CSRF 방지 및 교차 출처 요청 허용 설정
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // 4. RefreshToken을 Redis에 저장 (RT:이메일 주소 형태)
        // 7일(7 * 24 * 60 * 60 * 1000) 동안 보관


        // 5. 토큰 정보를 담은 DTO 반환
        return tokenDto;
    }
    public void logout(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0) // 핵심: 즉시 만료
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    @Transactional
    public TokenDto reissue(String accessToken,String refreshToken,
                                            HttpServletResponse response) {
        log.info("📢 재발급 요청 들어옴!");
        log.info("입력된 AccessToken: {}", accessToken);
        log.info("입력된 RefreshToken: {}", refreshToken);
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token에서 유저 정보 추출 (만료된 토큰이라도 Claims는 꺼낼 수 있음)
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email=authentication.getName();
        // 3. Redis에서 해당 유저의 Refresh Token 가져오기
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);


        // 4. 일치 여부 확인
        if (savedRefreshToken==null || !savedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("토큰 정보가 일치하지 않거나 이미 로그아웃된 사용자입니다.");
        }

        // 5. 새로운 토큰 생성
        String userName = memberService.findNameByEmail(email);
        // 이 안에서 새로운 RT를 Redis에 저장하고 쿠키도 새로 구워줍니다.
        TokenDto tokenDto = tokenProvider.generateToken(authentication, userName);
        String newRefreshToken= tokenProvider.refreshToken(email);
        // 6. Redis에 새로운 Refresh Token 업데이트 (기존 TTL 유지 또는 새로 설정)
         redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                 newRefreshToken,
                 7, TimeUnit.DAYS
//
        );
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false) // 로컬 테스트 시 false, 배포 시 true
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60) // 7일 등 적절한 기간
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return tokenDto;
    }
}
