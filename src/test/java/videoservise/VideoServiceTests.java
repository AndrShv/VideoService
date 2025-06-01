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

public class VideoServiceTests {

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


    @Test
    public void testGetVideoBIdIncrementsViewsAndReturnsResponse() {
        UUID id = videoSample.getId();
        when(videoRepository.findById(id)).thenReturn(Optional.of(videoSample));
        when(videoRepository.save(any(Video.class))).thenReturn(videoSample);

        VideoResponse response = videoService.getVideoBId(id);

        verify(videoRepository).findById(id);
        verify(videoRepository).save(videoSample);
        assertEquals(101L, videoSample.getViews());
        assertNotNull(response);
        assertEquals(videoSample.getTitle(), response.title());
        assertEquals(videoSample.getViews(), response.views());
    }


    @Test
    public void testGetVideoBId_VideoNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(videoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> videoService.getVideoBId(nonExistentId));
    }


    @Test
    public void test_getVideosByAuthor_returnsCorrectVideoResponses() {
        UUID authorId = UUID.randomUUID();
        List<Video> authorVideos = Arrays.asList(videoSample);

        when(videoRepository.findAllByAuthorId(authorId)).thenReturn(authorVideos);

        List<VideoResponse> result = videoService.getVideosByAuthor(authorId);

        assertEquals(1, result.size());
        assertEquals(videoSample.getId(), result.get(0).id());
        assertEquals(videoSample.getTitle(), result.get(0).title());
        assertEquals(videoSample.getAuthorId(), result.get(0).authorId());

        verify(videoRepository).findAllByAuthorId(authorId);
    }


    @Test
    public void testDeleteVideoUnauthorized() {
        UUID videoId = UUID.randomUUID();
        UUID videoAuthorId = UUID.randomUUID();
        UUID unauthorizedAuthorId = UUID.randomUUID();

        Video video = new Video();
        video.setId(videoId);
        video.setAuthorId(videoAuthorId);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        assertThrows(RuntimeException.class, () -> videoService.deleteVideo(videoId, unauthorizedAuthorId));

        verify(videoRepository, never()).delete(any(Video.class));
    }


    @Test
    public void testDeleteVideoWhenAuthorIdMatches() {
        UUID videoId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Video video = new Video();
        video.setId(videoId);
        video.setAuthorId(authorId);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        videoService.deleteVideo(videoId, authorId);

        verify(videoRepository).delete(video);
    }


    @Test
    public void testDeleteVideo_UnauthorizedUser() {
        UUID videoId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();

        Video video = new Video();
        video.setId(videoId);
        video.setAuthorId(authorId);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        assertThrows(RuntimeException.class, () -> videoService.deleteVideo(videoId, differentAuthorId));

        verify(videoRepository, never()).delete(any(Video.class));
    }


    @Test
    public void testFilterByDurationReturnsCorrectVideos() {
        Long duration = 150L;
        int limit = 10;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Video shortVideo = new Video();
        shortVideo.setDuration(100L);
        Video longVideo = new Video();
        longVideo.setDuration(200L);

        List<Video> allVideos = Arrays.asList(shortVideo, longVideo);
        when(videoRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(new PageImpl<>(allVideos));

        List<VideoResponse> result = videoService.FilterByDuration(duration, limit, offset);

        assertEquals(1, result.size());
        verify(videoRepository).findAllByOrderByCreatedAtDesc(pageable);
    }


    @Test
    public void testFilterByDurationWithNegativeOffset() {
        Long duration = 100L;
        int limit = 10;
        int negativeOffset = -1;

        Pageable pageable = PageRequest.of(0, limit);
        when(videoRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<VideoResponse> result = videoService.FilterByDuration(duration, limit, negativeOffset);

        assertTrue(result.isEmpty());
        verify(videoRepository).findAllByOrderByCreatedAtDesc(pageable);
    }


    @Test
    public void testFilterByDurationWithZeroLimit() {
        Long duration = 100L;
        int zeroLimit = 0;
        int offset = 0;

        Pageable pageable = PageRequest.of(0, 1);
        when(videoRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<VideoResponse> result = videoService.FilterByDuration(duration, zeroLimit, offset);

        assertTrue(result.isEmpty());
        verify(videoRepository).findAllByOrderByCreatedAtDesc(pageable);
    }


    @Test
    public void testGetPopularVideos() {
        int limit = 5;
        List<Video> popularVideos = Arrays.asList(videoSample, videoSample, videoSample);
        Page<Video> page = new PageImpl<>(popularVideos);

        when(videoRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))).thenReturn(page);

        List<VideoResponse> result = videoService.getPopularVideos(limit);

        assertEquals(3, result.size());
        for (VideoResponse response : result) {
            assertEquals(videoSample.getTitle(), response.title());
            assertEquals(videoSample.getId(), response.id());
            assertEquals(videoSample.getAuthorId(), response.authorId());
        }

        verify(videoRepository).findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }


    @Test
    public void testGetPopularVideos_NegativeLimit() {
        int negativeLimit = -1;
        when(videoRepository.findAllByOrderByCreatedAtDesc(any(PageRequest.class))).thenReturn(Page.empty());

        List<VideoResponse> result = videoService.getPopularVideos(negativeLimit);

        assertTrue(result.isEmpty());
        verify(videoRepository).findAllByOrderByCreatedAtDesc(PageRequest.of(0, 0));
    }


    @Test
    public void testUpdateVideoUnauthorized() {
        UUID videoId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();
        VideoRequest request = new VideoRequest("Updated Title", "Updated Description", 180L, "http://updated.video.url", "http://updated.thumbnail.url", "Updated Category");

        Video video = new Video();
        video.setId(videoId);
        video.setAuthorId(authorId);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        assertThrows(RuntimeException.class, () -> videoService.updateVideo(videoId, request, differentAuthorId));

        verify(videoRepository, never()).save(any(Video.class));
    }


    @Test
    public void testUpdateVideoWithAuthorizedUser() {
        UUID videoId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Video existingVideo = new Video();
        existingVideo.setId(videoId);
        existingVideo.setAuthorId(authorId);
        existingVideo.setTitle("Old Title");
        existingVideo.setDescription("Old Description");
        existingVideo.setDuration(100L);
        existingVideo.setVideoUrl("http://old.video.url");
        existingVideo.setThumbnailUrl("http://old.thumbnail.url");
        existingVideo.setCategory("Old Category");

        VideoRequest updateRequest = new VideoRequest(
            "New Title",
            "New Description",
            200L,
            "http://new.video.url",
            "http://new.thumbnail.url",
            "New Category"
        );

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(existingVideo));
        when(videoRepository.save(any(Video.class))).thenReturn(existingVideo);

        videoService.updateVideo(videoId, updateRequest, authorId);

        verify(videoRepository).save(existingVideo);
        assertEquals("New Title", existingVideo.getTitle());
        assertEquals("New Description", existingVideo.getDescription());
        assertEquals(200L, existingVideo.getDuration());
        assertEquals("http://new.video.url", existingVideo.getVideoUrl());
        assertEquals("http://new.thumbnail.url", existingVideo.getThumbnailUrl());
        assertEquals("New Category", existingVideo.getCategory());
    }


    @Test
    public void testUpdateVideo_UnauthorizedUser() {
        UUID videoId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();

        Video video = new Video();
        video.setId(videoId);
        video.setAuthorId(authorId);

        VideoRequest request = new VideoRequest(
            "Updated Title",
            "Updated Description",
            180L,
            "http://updated-video.url",
            "http://updated-thumbnail.url",
            "Updated Category"
        );

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        assertThrows(RuntimeException.class, () -> videoService.updateVideo(videoId, request, differentAuthorId));

        verify(videoRepository, never()).save(any(Video.class));
    }
}
