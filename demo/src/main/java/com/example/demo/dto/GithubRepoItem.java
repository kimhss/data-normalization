package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GithubRepoItem {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("stargazers_count")
    private Integer stargazersCount;

    @JsonProperty("forks_count")
    private Integer forksCount;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("default_branch")
    private String defaultBranch;

    @JsonProperty("pushed_at")
    private String pushedAt;

    @JsonProperty("topics")
    private List<String> topics;

    @JsonProperty("license")
    private String license;

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("raw_metadata")
    private String rawMetadata;

    @JsonProperty("files")
    private List<GithubFileItem> files;  // ← JSON의 files 배열 매핑

    @Getter
    @NoArgsConstructor
    public static class Owner {

        @JsonProperty("login")
        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("type")
        private String type;  // "User" or "Organization"
    }
}
