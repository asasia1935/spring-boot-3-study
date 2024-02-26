package com.example.springbootdeveloper.repository;

import com.example.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // FROM users WHERE email = #{email
    Optional<User> findByEmail(String email); // 이메일로 사용자 정보를 가져오는 함수

}
