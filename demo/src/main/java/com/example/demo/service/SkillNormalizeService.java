package com.example.demo.service;

import com.example.demo.SkillMarkdownParser;
import com.example.demo.dto.GithubFileItem;
import com.example.demo.dto.GithubRepoItem;
import com.example.demo.entity.Agent;
import com.example.demo.entity.Repository;
import com.example.demo.entity.Skill;
import com.example.demo.enums.OwnerType;
import com.example.demo.enums.Track;
import com.example.demo.repository.AgentRepository;
import com.example.demo.repository.RepositoryRepository;
import com.example.demo.repository.SkillRepository;
import com.theokanning.openai.client.OpenAiApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillNormalizeService {

    private final SkillMarkdownParser parser;
    private final RepositoryRepository repositoryRepository;
    private final SkillRepository skillRepository;
    private final AgentRepository agentRepository;

    @Transactional
    public Repository normalizeRepository(GithubRepoItem repoItem) {

        // 이미 존재하면 재사용
        return repositoryRepository.findByGithubId(repoItem.getId())
                .orElseGet(() -> {
                    Repository repo = Repository.builder()
                            .githubId(repoItem.getId())
                            .name(repoItem.getName())
                            .sourceRepo(repoItem.getFullName())
                            .sourceUri(repoItem.getHtmlUrl())
                            .summary(repoItem.getDescription())
                            .track(classifyTrack(repoItem))
                            .tagsJson(repoItem.getTopics() != null
                                    ? repoItem.getTopics().toString() : null)
                            .starCount(repoItem.getStargazersCount())
                            .forkCount(repoItem.getForksCount())
                            .size(repoItem.getSize())
                            .license(repoItem.getLicense() != null
                                    ? repoItem.getLicense() : null)
                            .homepage(repoItem.getHomepage())
                            .ownerAvatarUrl(repoItem.getOwner() != null
                                    ? repoItem.getOwner().getAvatarUrl() : null)
                            .ownerType(repoItem.getOwner() != null
                                    ? OwnerType.valueOf(repoItem.getOwner().getType().toUpperCase())
                                    : null)
                            .isOfficial(false)
                            .defaultBranch(repoItem.getDefaultBranch())
                            .sourceUpdatedAt(parseDateTime(repoItem.getPushedAt()))
                            .active(true)
                            .rawMetadata(repoItem.getRawMetadata())
                            .build();

                    return repositoryRepository.save(repo);
                });
    }

    @Transactional
    public Skill normalizeSkill(Repository repository, GithubFileItem fileItem, String rawContent) {

        String skillName = parser.extractSkillName(rawContent, fileItem.getName());

        // 이미 존재하면 content_hash 비교 후 업데이트
        return skillRepository.findByRepositoryIdAndSkillName(repository.getId(), skillName)
                .map(existing -> {
                    if (!existing.getContentHash().equals(fileItem.getSha())) {
                        existing.update(rawContent, fileItem.getSha());
                        log.info("스킬 업데이트: {}/{}", repository.getSourceRepo(), skillName);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Skill skill = Skill.builder()
                            .repository(repository)
                            .skillName(skillName)
                            .contentMd(rawContent)
                            .contentHash(fileItem.getSha())
                            .build();

                    return skillRepository.save(skill);
                });
    }

    @Transactional
    public Agent normalizeAgent(Repository repository, GithubFileItem fileItem, String rawContent) {

        // 레포당 하나만 존재, content_hash 비교 후 업데이트
        return agentRepository.findByRepositoryId(repository.getId())
                .map(existing -> {
                    if (!existing.getContentHash().equals(fileItem.getSha())) {
                        existing.update(rawContent, fileItem.getSha());
                        log.info("에이전트 업데이트: {}", repository.getSourceRepo());
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Agent agent = Agent.builder()
                            .repository(repository)
                            .contentMd(rawContent)
                            .contentHash(fileItem.getSha())
                            .build();

                    return agentRepository.save(agent);
                });
    }

    // 레포 정보 기반 track 분류
    private Track classifyTrack(GithubRepoItem repoItem) {
        String text = "";
        if (repoItem.getDescription() != null) text += repoItem.getDescription();
        if (repoItem.getTopics() != null) text += repoItem.getTopics().toString();
        text = text.toLowerCase();

        String[] beKeywords = {"backend", "server", "api", "database", "spring", "python", "django"};
        String[] feKeywords = {"frontend", "react", "vue", "css", "ui", "html", "tailwind"};

        long beScore = java.util.Arrays.stream(beKeywords).filter(text::contains).count();
        long feScore = java.util.Arrays.stream(feKeywords).filter(text::contains).count();

        if (beScore > 0 && feScore > 0) return Track.FULLSTACK;
        if (beScore > feScore) return Track.BACKEND;
        if (feScore > beScore) return Track.FRONTEND;
        return Track.FULLSTACK;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
}