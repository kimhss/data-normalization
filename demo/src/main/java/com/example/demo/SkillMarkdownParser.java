package com.example.demo;

import com.example.demo.enums.Track;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SkillMarkdownParser {

    // skill_name: h1 헤더 추출, 없으면 파일명
    public String extractSkillName(String content, String filename) {
        Pattern h1 = Pattern.compile("^#\\s+(.+)", Pattern.MULTILINE);
        Matcher m = h1.matcher(content);
        if (m.find()) return m.group(1).trim();
        return filename.replace(".md", "");
    }

    // summary: ## Description 섹션 추출
    public String extractSummary(String content) {
        Pattern desc = Pattern.compile(
                "##\\s+[Dd]escription\\s*\\n+([\\s\\S]+?)(?=\\n##|\\n#|$)"
        );
        Matcher m = desc.matcher(content);
        if (m.find()) {
            String summary = m.group(1).trim();
            return summary.length() > 500 ? summary.substring(0, 500) : summary;
        }
        // Description 섹션 없으면 h1 아래 첫 단락
        Pattern firstPara = Pattern.compile(
                "^#[^\\n]+\\n+([^#\\-\\n][^\\n]+)", Pattern.MULTILINE
        );
        Matcher m2 = firstPara.matcher(content);
        if (m2.find()) return m2.group(1).trim();
        return null;
    }

    // track: 키워드 스코어링으로 ENUM 분류
    public Track classifyTrack(String content, String path) {
        String text = (content + path).toLowerCase();
        String[] beKeywords = {"backend", "server", "api", "database", "spring", "python", "django"};
        String[] feKeywords = {"frontend", "react", "vue", "css", "ui", "html", "tailwind"};

        long beScore = Arrays.stream(beKeywords).filter(text::contains).count();
        long feScore = Arrays.stream(feKeywords).filter(text::contains).count();

        if (beScore > 0 && feScore > 0) return Track.FULLSTACK;
        if (beScore > feScore) return Track.BACKEND;
        if (feScore > beScore) return Track.FRONTEND;
        return Track.FULLSTACK;
    }

    // tags_json: 코드블록 언어 추출
    public List<String> extractTags(String content) {
        Pattern langPattern = Pattern.compile("```(\\w+)");
        Matcher matcher = langPattern.matcher(content);
        Set<String> tags = new LinkedHashSet<>();
        while (matcher.find()) tags.add(matcher.group(1).toLowerCase());
        List<String> result = new ArrayList<>(tags);
        return result.subList(0, Math.min(result.size(), 10));
    }
}
