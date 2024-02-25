package com.example.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 엔티티의 created_at, updated_at을 자동으로 업데이트
@SpringBootApplication
public class SpringbootDeveloperApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDeveloperApplication.class, args);
    }

}
