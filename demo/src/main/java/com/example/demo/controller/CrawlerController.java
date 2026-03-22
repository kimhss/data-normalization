package com.example.demo.controller;

import com.example.demo.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawler")
public class CrawlerController {

    private final CrawlerService crawlerService;

    @PostMapping("/run")
    public ResponseEntity<String> run() throws InterruptedException {
        crawlerService.run();
        return ResponseEntity.ok("크롤링 완료");
    }
}
