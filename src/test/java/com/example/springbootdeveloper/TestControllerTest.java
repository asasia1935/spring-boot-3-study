package com.example.springbootdeveloper;

import org.junit.jupiter.api.AfterEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 테스트용 애플리케이션 컨텍스트 생성 -> 스프링부트애플리케이션이 있는 클래스를 찾고 포함된 빈을 찾은 후에 컨텍스트를 만듬
@AutoConfigureMockMvc
// MockMvc를 생성하고 자동으로 구성하는 애너테이션.
// MockMvc는 애플리케이션을 서버에 배포하지 않고 테스트용 MVC 환경을 만들어서 요청 및 전송, 응답기능 제공하는 유틸리티 클래스
// (컨트롤러 클래스용)
class TestControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void mockMvcSetUp() { // MockMvc 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    public void cleanUp() { // member 테이블에 있는 데이터를 모두 삭제
        memberRepository.deleteAll();
    }

    @DisplayName("getAllMembers: 아티클 조회에 성공한다.")
    @Test
    public void getAllMembers() throws Exception {
        // given: 멤버를 저장합니다.
        final String url = "/test";
        Member savedMember = memberRepository.save(new Member(1L, "홍길동"));

        // when: 멤버 리스트를 조회하는 API를 호출합니다.
        final ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then: 응답 코드가 200 OK이고, 반환받은 값 중에 0번째 요소의 id, name이 저장된 값과 같은지 확인
        result
                .andExpect(status().isOk())
                // 응답의 0번째 값이 DB에 저장한 값과 같은지 확인
                .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
                .andExpect(jsonPath("$[0].name").value(savedMember.getName()));
    }
}