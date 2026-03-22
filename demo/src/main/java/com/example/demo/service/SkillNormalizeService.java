package com.example.demo.service;

import com.example.demo.SkillMarkdownParser;
import com.example.demo.dto.GithubFileItem;
import com.example.demo.entity.SkillAsset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SkillNormalizeService {

    private final SkillMarkdownParser parser;

    public SkillAsset normalize(GithubFileItem item, String rawContent) {
        return SkillAsset.builder()
                .skillName(parser.extractSkillName(rawContent, item.getName()))
                .summary(parser.extractSummary(rawContent))
                .track(parser.classifyTrack(rawContent, item.getPath()))
                .tagsJson(parser.extractTags(rawContent).toString())
                .sourceRepo(item.getRepository().getFullName())
                .sourceUrl(item.getHtmlUrl())
                .contentMd(rawContent)
                .fileSha(item.getSha())
                .starCount(item.getRepository().getStargazersCount())
                .forkCount(item.getRepository().getForksCount())
                .sourceUpdatedAt(parseDateTime(item.getRepository().getPushedAt()))
                .active(true)
                .build();
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
}
