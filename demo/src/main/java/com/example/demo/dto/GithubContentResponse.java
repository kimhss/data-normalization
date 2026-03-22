package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubContentResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("path")
    private String path;

    @JsonProperty("sha")
    private String sha;

    @JsonProperty("content")
    private String content;  // base64 인코딩된 파일 내용

    @JsonProperty("html_url")
    private String htmlUrl;
}
