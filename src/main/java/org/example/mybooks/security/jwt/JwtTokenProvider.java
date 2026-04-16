package org.example.mybooks.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.service.MemberService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.example.mybooks.dto.api.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j

public class JwtTokenProvider {
//    @Value("${jwt.secret}")
//    private String secretKey;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 1;//1분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;//7일
    private final RedisTemplate<String, String> redisTemplate; // 여기에 final이 꼭 붙어야 합니다.
    private final Key key;




    public JwtTokenProvider(
            RedisTemplate<String, String> redisTemplate,
            @Value("${jwt.secret}") String secretKey) {
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Authentication authentication, String userName) {
        String email = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .collect(Collectors.joining(","));
        String accessToken = createToken(email, roles);
        log.info("role:{}",roles);


        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .userName(userName)
                .role(roles)
                .build();
    }

    public String createToken(String email,String role){
        Claims claims=Jwts.claims().setSubject(email);
        claims.put("role",role);
        Date now=new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(this.key,SignatureAlgorithm.HS256)
                .compact();
    }
    public String refreshToken(String email){
        Claims claims=Jwts.claims().setSubject(email);
        Date now=new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(this.key,SignatureAlgorithm.HS256)
                .compact();
    }
    // 1. 토큰에서 인증 정보(유저객체, 권한)를 추출하는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
//        try {
//            claims = Jwts.parserBuilder()
//                    .setSigningKey(this.key)
//                    .build()
//                    .parseClaimsJws(accessToken)
//                    .getBody();
//        } catch (ExpiredJwtException e) {
//            // ⭐ 만료되어도 Claims 알맹이는 꺼낼 수 있습니다!
//            claims = e.getClaims();
//        }
        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기 (성현님이 만든 ROLE_ADMIN 등)
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 2. 토큰의 유효성 및 만료일자 검증 메서드
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");

        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");

        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");

        }
        return false;
    }
//
//    // 3. [내부 활용] 토큰 복호화 보조 메서드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰이지만 정보를 추출합니다.");
            return e.getClaims();
        }
    }
}
