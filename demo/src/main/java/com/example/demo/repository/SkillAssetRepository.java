package com.example.demo.repository;

import com.example.demo.entity.SkillAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillAssetRepository extends JpaRepository<SkillAsset, Long> {

    // source_url 기준 중복 체크
    boolean existsBySourceUrl(String sourceUrl);

    // source_url로 조회 (중복 시 기존 레코드 재사용)
    Optional<SkillAsset> findBySourceUrl(String sourceUrl);

    // 활성화된 skill_asset 전체 조회 (배치 업데이트용)
    List<SkillAsset> findAllByActiveTrue();
}
