package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GithubSearchResponse {

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("items")
    private List<GithubFileItem> items;
}
