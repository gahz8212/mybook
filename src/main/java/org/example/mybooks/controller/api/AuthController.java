package org.example.mybooks.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mybooks.dto.JoinDto;
import org.example.mybooks.dto.api.LoginRequestDto;
import org.example.mybooks.dto.api.TokenDto;
import org.example.mybooks.dto.api.TokenRequestDto;

import org.example.mybooks.security.jwt.JwtTokenProvider;
import org.example.mybooks.service.AuthService;
import org.example.mybooks.service.MemberService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Tag(name="User API",description = "사용자 인증 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String,String> redisTemplate;
    private final AuthService authService;
    private final MemberService memberService;

    @Operation(summary="회원가입",description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody JoinDto join,
                                       BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                // 어떤 검증 오류가 발생했는지 로그에 찍어줍니다.
                bindingResult.getAllErrors().forEach(error -> {
                    log.error("검증 오류: {}", error.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            log.info("회원가입 요청 데이터: {}",join.toString());
            memberService.join(join);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        }catch( Exception e){
            log.error("회원가입 로직 실행 중 에러 발생: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary="회원인증",description = "회원 인증")
    @PostMapping("/login") // 프론트엔드 axios.post('/api/login', ...)과 정확히 일치
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        log.info("📢 [성공] 컨트롤러 진입! 이메일: {}", loginRequest.getEmail());
        TokenDto token=authService.login(loginRequest,response);
        log.info("token:{}",token);

        // 일단은 성공 응답만 보내서 통신 확인
        return ResponseEntity.ok(token);
    }
    @Operation(summary = "로그 아웃",description = "회원이 로그아웃을 합니다.")
    @PostMapping("/logout")
    public void logout(HttpServletResponse response){
        authService.logout(response);
    }
    // UserController.java (예시)
    @GetMapping("/user/me")
    public ResponseEntity<?> getMyInfo(Principal principal) {
        // Principal 객체는 이미 인증된 사용자의 정보를 담고 있습니다.
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String email = principal.getName(); // 여기서 getName()은 무조건 작동합니다.
        return ResponseEntity.ok("인증 성공! 이메일: " + email);
    }
    @Operation(summary = "토큰을 재 발행",description = "401에러 발생시 토큰을 재 발행 합니다.")
    @PostMapping("/reissue") // 반드시 Post이어야 합니다.
    public ResponseEntity<TokenDto> reissue(HttpServletRequest request,
                                            @CookieValue(name="refreshToken") String refreshToken,
                                            HttpServletResponse response) {
        String bearerToken=request.getHeader("Authorization");
        String accessToken=bearerToken.substring(7);
        return ResponseEntity.ok(authService.reissue(accessToken,refreshToken,response));
    }
}
