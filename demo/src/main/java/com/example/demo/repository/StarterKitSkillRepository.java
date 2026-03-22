package com.example.demo.repository;

import com.example.demo.entity.StarterKitSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarterKitSkillRepository extends JpaRepository<StarterKitSkill, Long> {

    // 특정 starter_kit에 포함된 스킬 목록 조회
    List<StarterKitSkill> findAllByStarterKitIdOrderBySortOrderAsc(Long starterKitId);

    // 중복 체크 추가
    boolean existsByStarterKitIdAndSkillAssetId(Long starterKitId, Long skillAssetId);
}
