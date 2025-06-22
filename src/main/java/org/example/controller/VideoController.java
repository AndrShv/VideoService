package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.DTO.request.VideoRequest;
import org.example.DTO.response.VideoResponse;
import org.example.service.MsgConsumer;
import org.example.service.VideoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final MsgConsumer msgConsumer;

    @PostMapping("/create-new-video")
    public ResponseEntity<VideoResponse> createVideo(
            @RequestBody VideoRequest request,
            @RequestHeader("X-Author-Id") UUID authorId
    ) {
        VideoResponse response = videoService.createVideo(request, authorId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable UUID id) {
        VideoResponse response = videoService.getVideoById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<VideoResponse>> getFeed(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<VideoResponse> feed = videoService.getFeed(limit, offset);
        return ResponseEntity.ok(feed);
    }

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

    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<VideoResponse>> getVideosByAuthor(@PathVariable UUID authorId) {
        List<VideoResponse> videos = videoService.getVideosByAuthor(authorId);
        return ResponseEntity.ok(videos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable UUID id, @RequestHeader("X-Author-Id") UUID authorId) {
        videoService.deleteVideo(id, authorId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVideo(
            @PathVariable UUID id,
            @RequestBody VideoRequest request,
            @RequestHeader("X-Author-Id") UUID authorId) {
        videoService.updateVideo(id, request, authorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<VideoResponse>> getPopularVideos(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(videoService.getPopularVideos(limit));
    }


}

