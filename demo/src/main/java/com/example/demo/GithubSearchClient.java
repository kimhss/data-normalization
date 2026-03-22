package com.example.demo;

import com.example.demo.dto.GithubContentResponse;
import com.example.demo.dto.GithubFileItem;
import com.example.demo.dto.GithubSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

// GithubClient.java
@Component
@RequiredArgsConstructor
public class GithubSearchClient {

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    // GitHub API 직접 호출
    public GithubSearchResponse search(String query, int page) {
        String url = baseUrl + "/search?q=" + query + "&per_page=100&page=" + page;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/vnd.github+json");

        ResponseEntity<GithubSearchResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GithubSearchResponse.class
        );

        return response.getBody();
    }

    // 파일 내용 fetch (base64 디코딩)
    public String fetchContent(String contentsUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/vnd.github+json");

        ResponseEntity<GithubContentResponse> response = restTemplate.exchange(
                contentsUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GithubContentResponse.class
        );

        String encoded = response.getBody().getContent()
                .replaceAll("\\s", ""); // base64에 포함된 개행 제거

        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }
}