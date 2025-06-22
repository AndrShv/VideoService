package org.example.DTO.request;

public record VideoRequest(
        String title,
        String description,
        Long duration,
        String videoUrl,
        String thumbnailUrl,
        String category
){ }
