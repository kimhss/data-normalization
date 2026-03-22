package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubFileItem {

    @JsonProperty("name")
    private String name;  // "SKILL.md"

    @JsonProperty("path")
    private String path;  // "skills/react/SKILL.md"

    @JsonProperty("sha")
    private String sha;   // 파일 변경 감지용

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("content")
    private String content;  // 파일 본문 (plain text)
}
