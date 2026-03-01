package com.viberoom.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class YoutubeService {

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.googleapis.com/youtube/v3")
            .build();

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> search(String query) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("part", "snippet")
                            .queryParam("q", query + " official audio")
                            .queryParam("type", "video")
                            .queryParam("videoCategoryId", "10")
                            .queryParam("maxResults", "8")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return List.of();
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items == null) return List.of();

            List<Map<String, Object>> results = new ArrayList<>();
            for (Map<String, Object> item : items) {
                try {
                    Map<String, Object> id      = (Map<String, Object>) item.get("id");
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    Map<String, Object> thumbs  = (Map<String, Object>) snippet.get("thumbnails");
                    Map<String, Object> medium  = (Map<String, Object>) thumbs.get("medium");
                    results.add(Map.of(
                            "youtubeId", id.get("videoId"),
                            "title",     snippet.get("title"),
                            "artist",    snippet.get("channelTitle"),
                            "thumbnail", medium.get("url"),
                            "duration",  "0:00"
                    ));
                } catch (Exception e) {
                    System.err.println("Error parsing item: " + e.getMessage());
                }
            }
            return results;
        } catch (Exception e) {
            System.err.println("YouTube search error: " + e.getMessage());
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getVideo(String videoId) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uri -> uri.path("/videos")
                            .queryParam("part", "snippet,contentDetails")
                            .queryParam("id", videoId)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items == null || items.isEmpty()) return Map.of();

            Map<String, Object> item    = items.get(0);
            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
            Map<String, Object> details = (Map<String, Object>) item.get("contentDetails");

            return Map.of(
                    "youtubeId", videoId,
                    "title",     snippet.get("title"),
                    "artist",    snippet.get("channelTitle"),
                    "duration",  details.get("duration")
            );
        } catch (Exception e) {
            return Map.of();
        }
    }
}
