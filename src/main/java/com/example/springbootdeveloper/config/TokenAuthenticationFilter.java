package com.example.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.example.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 토큰 필터에서 유효한 토큰인지 확인하고 맞을 경우 컨텍스트 홀더에 저장 -> 서비스 로직 실행
// 컨텍스트 홀더는 언제든지 인증 객체를 꺼낼 수 있는데 이는 스레드마다 공간을 할당하는 즉 스레드 로컬에 저장 -> 아무곳에나 참조 가능
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter { // 필터
    private final TokenProvider tokenProvider;

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        // 가져온 토큰이 유효한지 확인하고 유효할 때 인증 정보 설정 -> 시큐리티 컨텍스트에 설정
        if (tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication); // 유저 객체 반환
            // 유저 이름이나 권한 목록 같은 인증 정보 포함
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) { // 접두사 제거하는 함수
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        // null이거나 Bearer로 시작하지 않으면 null 반환
        return null;
    }
}