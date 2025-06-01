package videoservice.DTO.response;

import java.util.UUID;

public record VideoResponse(
        UUID id,
        UUID authorId,
        String title,
        String description,
        String videoUrl,
        String thumbnailUrl,
        Long duration,
        String category,
        Long views,
        Long likes,
        Long comments,
        String createdAt
) {}


