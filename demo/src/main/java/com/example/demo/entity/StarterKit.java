package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "starter_kits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class StarterKit extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;  // 생성 사용자 (크롤링 시 null 가능)

    @Column(name = "title", nullable = false, length = 150)
    private String title;  // "octocat/my-repo"

    @Column(name = "input_snapshot_json", columnDefinition = "TEXT")
    private String inputSnapshotJson;  // 포함된 skill id 목록

    @OneToMany(mappedBy = "starterKit",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<StarterKitSkill> starterKitSkills = new ArrayList<>();

    // skill id 목록 업데이트
    public void updateSnapshotJson(List<Long> skillIds) {
        List<Long> distinct = skillIds.stream().distinct().toList();
        this.inputSnapshotJson = distinct.toString();
    }

    // 스킬 매핑 추가
    public void addSkill(StarterKitSkill skill) {
        this.starterKitSkills.add(skill);
        skill.assignStarterKit(this);
    }
}
