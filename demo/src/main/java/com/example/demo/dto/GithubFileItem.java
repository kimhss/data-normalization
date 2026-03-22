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
    private String htmlUrl;  // GitHub 웹 URL

    @JsonProperty("url")
    private String url;  // Contents API URL (파일 내용 fetch 시 사용)

    @JsonProperty("repository")
    private Repository repository;

    @Getter
    @NoArgsConstructor
    public static class Repository {

        @JsonProperty("full_name")
        private String fullName;  // "octocat/my-repo"

        @JsonProperty("stargazers_count")
        private Integer stargazersCount;

        @JsonProperty("forks_count")
        private Integer forksCount;

        @JsonProperty("pushed_at")
        private String pushedAt;
    }
}
