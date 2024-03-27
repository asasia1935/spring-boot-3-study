package com.example.springbootdeveloper.config.jwt;

import com.example.springbootdeveloper.domain.User;
import com.example.springbootdeveloper.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given: 토큰에 유저 정보를 추가하기 위한 테스트 유저를 만듭니다.
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when: 토큰 제공자인 generateToken() 메서드를 호출해서 토큰을 만듭니다.
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then: jjwt 라이브러리를 사용해 토큰을 복호화 합니다.
        // 이때 토큰에 넣은 클레임의 id값이 위에서 만든 유저 id와 동일한지 체크합니다.
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken(): 만료된 토큰인 경우에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given: jjwt 라이브러리를 사용해 토큰 생성, 이때 현재 시간을 치환한 값에서 1000을 빼서 만료된 토큰으로 만듭니다
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when: 토큰 제공자인 generateToken() 메서드를 호출해 유효한 토큰인지 검증하고 결과값 반환합니다.
        boolean result = tokenProvider.validToken(token);

        // then: 반환값이 false인지를 확인합니다.
        assertThat(result).isFalse();
    }


    @DisplayName("validToken(): 유효한 토큰인 경우에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given: jjwt 라이브러리를 사용하여 토큰 생성, 만료 시간은 14일 뒤로 만료되지 않은 토큰으로 생성합니다.
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when: 토큰 제공자인 generateToken() 메서드를 호출해 유효한 토큰인지 검증하고 결과값 반환합니다.
        boolean result = tokenProvider.validToken(token);

        // then: 반환값이 true인지 확인합니다.
        assertThat(result).isTrue();
    }


    @DisplayName("getAuthentication(): 토큰 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given: jjwt 라이브러리를 사용해 토큰을 생성합니다. 토큰의 제목인 subject는 이메일을 사용합니다.
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when: 토큰 제공자인 generateToken() 메서드를 호출해 인증 객체를 반환받습니다.
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then: 반환받은 인증 객체인 유저 이름을 가져와서 설정한 값과 같은지 확인합니다.
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given: jjwt 라이브러리를 사용해 토큰을 생성, 클레임을 추가하되 키는 id, 값은 1이라는 유저 iD입니다.
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when: 토큰 제공자인 generateToken() 메서드를 호출해 유저 ID를 반환받습니다.
        Long userIdByToken = tokenProvider.getUserId(token);

        // then: 반환받은 유저 ID가 위에서 설정한 유저 ID값인 1과 같은지 확인합니다.
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
