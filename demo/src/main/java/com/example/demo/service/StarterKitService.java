package com.example.demo.service;

import com.example.demo.dto.GithubFileItem;
import com.example.demo.entity.SkillAsset;
import com.example.demo.entity.StarterKit;
import com.example.demo.entity.StarterKitSkill;
import com.example.demo.repository.SkillAssetRepository;
import com.example.demo.repository.StarterKitRepository;
import com.example.demo.repository.StarterKitSkillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarterKitService {

    private final SkillAssetRepository skillAssetRepository;
    private final StarterKitRepository starterKitRepository;
    private final StarterKitSkillRepository starterKitSkillRepository;
    private final SkillNormalizeService normalizeService;

    @Transactional
    public void processRepo(String repoFullName, List<GithubFileItem> items, List<String> rawContents) {

        if (items.size() < 2) {
            log.info("단일 파일 레포 스킵: {}", repoFullName);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            GithubFileItem item = items.get(i);
            String content = rawContents.get(i);

            SkillAsset asset = skillAssetRepository
                    .findBySourceUrl(item.getHtmlUrl())
                    .orElseGet(() -> skillAssetRepository.save(
                            normalizeService.normalize(item, content)
                    ));
        }
    }
}