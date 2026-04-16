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
       try {
           HttpServletRequest httpRequest = (HttpServletRequest) request;
           HttpServletResponse httpResponse = (HttpServletResponse) response;

           String path = httpRequest.getRequestURI();
           log.info("📢 요청 들어옴!! URI: {}, Method: {}", path, httpRequest.getMethod());

           // 1. 로그인/회원가입 경로는 토큰 검증 없이 바로 통과 (가장 안전한 방법)
           if (path.startsWith("/api/login") || path.startsWith("/api/join")|| path.contains("reissue")) {
               log.info("✅ 검증 없이 통과하는 경로: {}", path);
               chain.doFilter(request, response);
               return;
           }

           String token = resolveToken(httpRequest);
           log.info("추출된 토큰: {}", token);

           try {
               // 2. 토큰이 있는 경우에만 검증 진행
               if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

//                    Redis 블랙리스트 체크 (토큰이 확실히 있을 때만 수행)
                   String isLogout = redisTemplate.opsForValue().get("blacklist:" + token);

                   if (!StringUtils.hasText(isLogout)) {
                       Authentication authentication = jwtTokenProvider.getAuthentication(token);
                       SecurityContextHolder.getContext().setAuthentication(authentication);
                       log.info("SecurityContext에 인증 정보 저장 완료");
                   }
               }
           } catch (ExpiredJwtException e) {
               log.warn("토큰 만료: {}", e.getMessage());
               httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               httpResponse.setContentType("application/json;charset=UTF-8");
               httpResponse.getWriter().write("{\"code\":\"401\", \"message\":\"ACCESS_TOKEN_EXPIRED\"}");
               return;
           } catch (Exception e) {
               log.error("JWT 필터 내 에러 발생: ", e);
               // 에러가 나더라도 다음 필터로 넘겨주거나 적절한 응답을 줘야 /error로 안 빠집니다.
           }

           chain.doFilter(request, response);
       }catch(Exception e){
           log.error("e:",e);
           throw e;
       }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // 👈 오타 주의 (Authorizaiton 등)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 값만 추출
        }
        return null;
    }
}
