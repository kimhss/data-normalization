package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "starter_kit_skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class StarterKitSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "starter_kit_id", nullable = false)
    private StarterKit starterKit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_asset_id", nullable = false)
    private SkillAsset skillAsset;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    // StarterKit 연관관계 편의 메서드
    public void assignStarterKit(StarterKit starterKit) {
        this.starterKit = starterKit;
    }
}
