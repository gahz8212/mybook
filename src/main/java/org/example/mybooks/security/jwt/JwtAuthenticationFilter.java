package org.example.mybooks.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        log.info("====== 필터 진입 성공 ======"); // 1. 이게 찍히는지 확인
        String token = resolveToken(httpRequest);
        log.info("추출된 토큰: {}", token); // 이게 null이면 프론트엔드 헤더 설정 문제!

        try {
            // 1. 토큰이 있고 유효한지 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                log.info("검증 로직 시작"); // 3. 여기까지 오는지 확인
                // 블랙리스트(로그아웃) 체크
                String isLogout = redisTemplate.opsForValue().get("blacklist:" + token);

                if (ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            // ⭐ 핵심: 토큰 만료 시 401 에러 응답을 직접 작성합니다.
            // 이 메시지가 나가야 프론트엔드 Axios 인터셉터의 catch(error)로 들어갑니다.
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":\"401\", \"message\":\"ACCESS_TOKEN_EXPIRED\"}");
            return; // 필터 체인을 더 이상 진행하지 않고 여기서 응답을 종료합니다.
        } catch (Exception e) {
            // 그 외 토큰 관련 에러 처리 (선택사항)
            log.error("JWT 검증 중 오류 발생: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
