package com.example.demo.repository;

import com.example.demo.entity.StarterKit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StarterKitRepository extends JpaRepository<StarterKit, Long> {

    // 레포명 기준 중복 체크
    boolean existsByTitle(String title);

    // 레포명으로 조회
    Optional<StarterKit> findByTitle(String title);
}
