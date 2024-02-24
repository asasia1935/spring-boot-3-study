package com.example.springbootdeveloper.service;

import com.example.springbootdeveloper.domain.Article;
import com.example.springbootdeveloper.dto.AddArticleRequest;
import com.example.springbootdeveloper.dto.UpdateArticleRequest;
import com.example.springbootdeveloper.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // 빈을 생성자로 생성 (final, @NotNull 키워드가 붙은 필드로 생성자 생성)
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 블로그 글 전체 조회 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 조회 메서드
    public Article findById(long id) {
        return blogRepository.findById(id) // ID를 받아 엔티티를 조회하고 없으면 예외 발생
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 블로그 글 삭제 메서드
    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    // 블로그 글 수정 메서드
    @Transactional // 트랜잭션 메서드 -> 메서드를 하나의 트랜잭션으로 묶는 역할
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
