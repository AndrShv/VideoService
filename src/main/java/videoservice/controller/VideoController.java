package videoservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import videoservice.DTO.request.VideoRequest;
import videoservice.DTO.response.VideoResponse;
import videoservice.service.VideoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // Создать новое видео (upload)
    @PostMapping
    public ResponseEntity<VideoResponse> createVideo(
            @RequestBody VideoRequest request,
            @RequestHeader("X-Author-Id") UUID authorId
    ) {
        VideoResponse response = videoService.createVideo(request, authorId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Получить видео по ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable UUID id) {
        VideoResponse response = videoService.getVideoById(id);
        return ResponseEntity.ok(response);
    }

    // Получить ленту видео с пагинацией
    @GetMapping("/feed")
    public ResponseEntity<List<VideoResponse>> getFeed(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<VideoResponse> feed = videoService.getFeed(limit, offset);
        return ResponseEntity.ok(feed);
    }

    // Поиск видео по названию и/или категории с пагинацией
    @GetMapping("/search")
    public ResponseEntity<List<VideoResponse>> searchVideos(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<VideoResponse> results = videoService.search(title, category, limit, offset);
        return ResponseEntity.ok(results);
    }
}
