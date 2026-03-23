package com.example.demo.service;

import com.example.demo.dto.GithubFileItem;
import com.example.demo.dto.GithubRepoItem;
import com.example.demo.entity.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService {

    private final SkillNormalizeService normalizeService;
    private final ObjectMapper objectMapper;

    private static final String SKILLS_BASE_PATH = "data/prompts/skills";
    private static final String AGENTS_BASE_PATH = "data/prompts/agents";

    public void run() {
        log.info("크롤링 시작");
        processDirectory(SKILLS_BASE_PATH, false);
        processDirectory(AGENTS_BASE_PATH, true);
    }

    private void processDirectory(String basePath, boolean isAgent) {
        File baseDir = new File(basePath);

        if (!baseDir.exists() || !baseDir.isDirectory()) {
            log.error("폴더가 존재하지 않습니다: {}", baseDir.getAbsolutePath());
            return;
        }

        // track 폴더 순회 (FRONTEND, BACKEND, FULLSTACK)
        File[] trackDirs = baseDir.listFiles(File::isDirectory);
        if (trackDirs == null) return;

        for (File trackDir : trackDirs) {
            log.info("[{}] track 처리 시작: {}", isAgent ? "AGENT" : "SKILL", trackDir.getName());

            File[] jsonFiles = trackDir.listFiles(
                    (dir, name) -> name.endsWith(".json")
            );
            if (jsonFiles == null) continue;

            for (File jsonFile : jsonFiles) {
                try {
                    processFile(jsonFile, isAgent);
                } catch (Exception e) {
                    log.error("파일 처리 실패: {}", jsonFile.getName(), e);
                }
            }
        }
    }

    private void processFile(File file, boolean isAgent) {
        log.info("파일 처리: {}", file.getPath());

        try {
            String json = Files.readString(file.toPath());
            GithubRepoItem repoItem = objectMapper.readValue(json, GithubRepoItem.class);

            // 누락 케이스 처리
            if (repoItem.getFullName() == null || repoItem.getFullName().isBlank()) {
                log.warn("full_name 누락 스킵: {}", file.getName());
                return;
            }

            if (repoItem.getFiles() == null || repoItem.getFiles().isEmpty()) {
                log.warn("파일 목록 없음: {}", repoItem.getFullName());
                return;
            }

            Repository repository = normalizeService.normalizeRepository(repoItem);

            for (GithubFileItem fileItem : repoItem.getFiles()) {
                String rawContent = fileItem.getContent();
                // content 누락 케이스 처리
                if (rawContent == null || rawContent.isBlank()) {
                    log.warn("content 없음: {}/{}", repoItem.getFullName(), fileItem.getPath());
                    continue;
                }

                if (isAgent) {
                    normalizeService.normalizeAgent(repository, fileItem, rawContent);
                    log.info("Agent 저장 완료: {}", repoItem.getFullName());
                } else {
                    normalizeService.normalizeSkill(repository, fileItem, rawContent);
                    log.info("Skill 저장 완료: {}/{}", repoItem.getFullName(), fileItem.getPath());
                }
            }

        } catch (IOException e) {
            // 오염 케이스 처리 (JSON 파싱 실패)
            log.error("JSON 파싱 실패: {}", file.getName(), e);
        } catch (Exception e) {
            log.error("파일 처리 실패: {}", file.getName(), e);
        }
    }

    // OCI Object Storage 연동 시 이 메서드만 교체
    private GithubRepoItem readFromOci(String bucket, String objectName) throws IOException {
        // TODO: OCI SDK 연동
        return null;
    }
}