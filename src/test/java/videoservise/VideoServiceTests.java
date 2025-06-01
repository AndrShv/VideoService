package videoservise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.PageImpl;

import videoservice.DTO.request.VideoRequest;
import videoservice.DTO.response.VideoResponse;
import videoservice.entity.Video;
import videoservice.repository.VideoRepository;
import videoservice.service.VideoService;

import java.time.LocalDateTime;
import java.util.*;

class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video videoSample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoSample = new Video();
        videoSample.setId(UUID.randomUUID());
        videoSample.setAuthorId(UUID.randomUUID());
        videoSample.setTitle("Test video");
        videoSample.setDescription("Description");
        videoSample.setVideoUrl("http://video.url");
        videoSample.setThumbnailUrl("http://thumbnail.url");
        videoSample.setDuration(120L);
        videoSample.setCategory("Games");
        videoSample.setViews(100L);
        videoSample.setLikes(10L);
        videoSample.setComments(5L);
        videoSample.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateVideo() {
        VideoRequest request = new VideoRequest(
                "New Video",
                "New description",
                200L,
                "http://newvideo.url",
                "http://newthumbnail.url",
                "Music"
        );
        UUID authorId = UUID.randomUUID();

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);

        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video v = invocation.getArgument(0);
            v.setId(UUID.randomUUID());
            v.setCreatedAt(LocalDateTime.now());
            return v;
        });

        VideoResponse response = videoService.createVideo(request, authorId);

        verify(videoRepository).save(videoCaptor.capture());
        Video savedVideo = videoCaptor.getValue();

        assertEquals(request.title(), savedVideo.getTitle());
        assertEquals(request.description(), savedVideo.getDescription());
        assertEquals(request.duration(), savedVideo.getDuration());
        assertEquals(request.videoUrl(), savedVideo.getVideoUrl());
        assertEquals(request.thumbnailUrl(), savedVideo.getThumbnailUrl());
        assertEquals(request.category(), savedVideo.getCategory());
        assertEquals(authorId, savedVideo.getAuthorId());

        assertNotNull(response.id());
        assertEquals(request.title(), response.title());
        assertEquals(request.category(), response.category());
    }

    @Test
    void testGetVideoById_Found() {
        UUID id = videoSample.getId();
        when(videoRepository.findById(id)).thenReturn(Optional.of(videoSample));

        VideoResponse response = videoService.getVideoById(id);

        assertEquals(videoSample.getTitle(), response.title());
        assertEquals(videoSample.getAuthorId(), response.authorId());
    }

    @Test
    void testGetVideoById_NotFound() {
        UUID id = UUID.randomUUID();
        when(videoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> videoService.getVideoById(id));
    }

    @Test
    void testGetFeed() {
        int limit = 2;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Video> videos = List.of(videoSample);

        when(videoRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(videos, pageable, videos.size()));
        List<VideoResponse> feed = videoService.getFeed(limit, offset);

        assertEquals(1, feed.size());
        assertEquals(videoSample.getTitle(), feed.get(0).title());
    }


    @Test
    void testSearch_ByTitleAndCategory() {
        String title = "test";
        String category = "games";
        int limit = 5;
        int offset = 0;

        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Video> videos = List.of(videoSample);

        // Оборачиваем список в PageImpl
        when(videoRepository.findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(title, category, pageable))
                .thenReturn(new PageImpl<>(videos, pageable, videos.size()));

        List<VideoResponse> results = videoService.search(title, category, limit, offset);

        assertEquals(1, results.size());
        assertEquals(videoSample.getTitle(), results.get(0).title());
    }

    @Test
    void testSearch_ByTitleOnly() {
        String title = "test";
        int limit = 5;
        int offset = 0;

        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Video> videos = List.of(videoSample);

        when(videoRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(new PageImpl<>(videos, pageable, videos.size()));

        List<VideoResponse> results = videoService.search(title, null, limit, offset);

        assertEquals(1, results.size());
    }

    @Test
    void testSearch_ByCategoryOnly() {
        String category = "games";
        int limit = 5;
        int offset = 0;

        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Video> videos = List.of(videoSample);

        when(videoRepository.findByCategoryIgnoreCase(category, pageable)).thenReturn(new PageImpl<>(videos, pageable, videos.size()));

        List<VideoResponse> results = videoService.search(null, category, limit, offset);

        assertEquals(1, results.size());
    }

    @Test
    void testSearch_NoParams() {
        int limit = 5;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Video> videos = List.of(videoSample);

        when(videoRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(new PageImpl<>(videos, pageable, videos.size()));

        List<VideoResponse> results = videoService.search(null, null, limit, offset);

        assertEquals(1, results.size());
    }
}
