package com.viberoom.backend.controller;

import com.viberoom.backend.service.YoutubeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/youtube")
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam String q) {
        return ResponseEntity.ok(youtubeService.search(q));
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<Map<String, Object>> getVideo(@PathVariable String id) {
        return ResponseEntity.ok(youtubeService.getVideo(id));
    }
}
