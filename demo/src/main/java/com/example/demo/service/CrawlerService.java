package com.example.demo.service;

import com.example.demo.GithubSearchClient;
import com.example.demo.dto.GithubFileItem;
import com.example.demo.dto.GithubSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService {

    private final GithubSearchClient githubSearchClient;
    private final SkillNormalizeService normalizeService;
    private final StarterKitService starterKitService;

    public void run() throws InterruptedException {

        // 1. SKILL.md + agents.md 검색
        List<GithubFileItem> allItems = new ArrayList<>();
        allItems.addAll(fetchAllPages("filename:SKILL.md"));
        allItems.addAll(fetchAllPages("filename:agents.md"));

        // 2. 레포별 그룹핑
        Map<String, List<GithubFileItem>> grouped = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRepository().getFullName()
                ));

        // 3. 레포별 처리
        for (Map.Entry<String, List<GithubFileItem>> entry : grouped.entrySet()) {
            String repoName = entry.getKey();
            List<GithubFileItem> repoItems = entry.getValue();

            // 4. 각 파일 내용 fetch
            List<String> contents = repoItems.stream()
                    .map(item -> githubSearchClient.fetchContent(item.getUrl()))
                    .toList();

            // 5. starter_kit + skill_assets 저장
            starterKitService.processRepo(repoName, repoItems, contents);

            log.info("처리 완료: {}", repoName);
            Thread.sleep(1500);
        }
    }

    private List<GithubFileItem> fetchAllPages(String query) throws InterruptedException {
        List<GithubFileItem> result = new ArrayList<>();
        int page = 1;

        while (page <= 10) {
            GithubSearchResponse response = githubSearchClient.search(query, page);

            if (response == null || response.getItems() == null
                    || response.getItems().isEmpty()) break;

            result.addAll(response.getItems());
            page++;
            Thread.sleep(1500);
        }

        return result;
    }
}
