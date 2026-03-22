package com.example.demo.entity;

import com.example.demo.enums.Track;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill_assets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SkillAsset extends BaseEntity {

    @Column(name = "skill_name", nullable = false, length = 150)
    private String skillName;

    @Column(name = "source_repo", nullable = false, length = 255)
    private String sourceRepo;

    @Column(name = "source_url", nullable = false, unique = true, length = 500)
    private String sourceUrl;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content_md", nullable = false, columnDefinition = "LONGTEXT")
    private String contentMd;

    @Enumerated(EnumType.STRING)
    @Column(name = "track", nullable = false)
    private Track track;

    @Column(name = "tags_json", columnDefinition = "JSON")
    private String tagsJson;

    @Column(name = "star_count")
    private Integer starCount;

    @Column(name = "fork_count")
    private Integer forkCount;

    @Column(name = "file_sha", length = 100)
    private String fileSha;  // 변경 감지용 SHA

    @Column(name = "source_updated_at")
    private LocalDateTime sourceUpdatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "skillAsset", fetch = FetchType.LAZY)
    @Builder.Default
    private List<StarterKitSkill> starterKitSkills = new ArrayList<>();

    // 변경 감지 후 업데이트
    public void update(String contentMd, String summary, Track track,
                       String tagsJson, Integer starCount, Integer forkCount,
                       String fileSha, LocalDateTime sourceUpdatedAt) {
        this.contentMd = contentMd;
        this.summary = summary;
        this.track = track;
        this.tagsJson = tagsJson;
        this.starCount = starCount;
        this.forkCount = forkCount;
        this.fileSha = fileSha;
        this.sourceUpdatedAt = sourceUpdatedAt;
    }

    public void deactivate() {
        this.active = false;
    }
}
