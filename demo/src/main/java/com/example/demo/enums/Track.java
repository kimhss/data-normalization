package com.example.demo.enums;

import java.util.Arrays;

public enum Track {

    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    FULLSTACK("풀스택");

    private final String description;

    Track(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // String → Track 변환 (대소문자 무시)
    public static Track from(String value) {
        if (value == null) return FULLSTACK; // 기본값

        return Arrays.stream(Track.values())
                .filter(track -> track.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "유효하지 않은 Track 값입니다: " + value
                ));
    }
}
