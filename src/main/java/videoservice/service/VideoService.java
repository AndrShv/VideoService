package videoservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import videoservice.DTO.request.VideoRequest;
import videoservice.DTO.response.VideoResponse;
import videoservice.entity.Video;
import videoservice.repository.VideoRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public VideoResponse createVideo(VideoRequest request, UUID authorId){
        Video video = new Video();
        video.setTitle(request.title());
        video.setDescription(request.description());
        video.setDuration(request.duration());
        video.setVideoUrl(request.videoUrl());
        video.setThumbnailUrl(request.thumbnailUrl());
        video.setCategory(request.category());
        video.setAuthorId(authorId);
        videoRepository.save(video);
        return toResponse(video);
    }
    public VideoResponse getVideoById(UUID id){
        Video video = videoRepository.findById(id).orElseThrow();
        return toResponse(video);
    }

    public List<VideoResponse> getFeed(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return videoRepository.findAllByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VideoResponse> search(String title, String category, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        if (title != null && category != null) {
            return videoRepository.findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(title, category, pageable)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } else if (title != null) {
            return videoRepository.findByTitleContainingIgnoreCase(title, pageable)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } else if (category != null) {
            return videoRepository.findByCategoryIgnoreCase(category, pageable)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } else {
            return getFeed(limit, offset);
        }
    }


    private VideoResponse toResponse(Video video) {
        return new VideoResponse(
                video.getId(),
                video.getAuthorId(),
                video.getTitle(),
                video.getDescription(),
                video.getVideoUrl(),
                video.getThumbnailUrl(),
                video.getDuration(),
                video.getCategory(),
                video.getViews(),
                video.getLikes(),
                video.getComments(),
                video.getCreatedAt().format(formatter)
        );
    }
}
