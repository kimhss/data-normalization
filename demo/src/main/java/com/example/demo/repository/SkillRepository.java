package com.example.demo.repository;

import com.example.demo.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    // 레포에 속한 스킬 전체 조회
    List<Skill> findAllByRepositoryId(Long repositoryId);

    // content_hash 기준 변경 감지
    Optional<Skill> findByRepositoryIdAndSkillName(Long repositoryId, String skillName);

    // 특정 레포의 스킬 존재 여부
    boolean existsByRepositoryIdAndSkillName(Long repositoryId, String skillName);
}