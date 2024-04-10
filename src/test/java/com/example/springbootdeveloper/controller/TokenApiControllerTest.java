package com.example.springbootdeveloper.controller;

import com.example.springbootdeveloper.config.jwt.JwtFactory;
import com.example.springbootdeveloper.config.jwt.JwtProperties;
import com.example.springbootdeveloper.domain.RefreshToken;
import com.example.springbootdeveloper.domain.User;
import com.example.springbootdeveloper.dto.CreateAccessTokenRequest;
import com.example.springbootdeveloper.repository.RefreshTokenRepository;
import com.example.springbootdeveloper.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given: 테스트 유저 생성 및 jjwt 라이브러리로 리프레시 토큰 생성후 DB에 저장
        // 토큰 생성 API의 요청 본문에 리프레시 토큰을 포함해서 요청 객체 생성
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToekn = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToekn));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToekn);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when: 토큰 추가 API에 요청(요청 타입은 제이슨이고 위에서 만든 객체를 요청 본문으로 함께 보냄)
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then: 응답 코드가 201 Created인지 확인하고 응답으로 온 액세스 토큰이 비어있지 않은지 확인
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

}
