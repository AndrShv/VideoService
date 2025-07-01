package org.example.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.CommentCreatedEvent;
import org.example.repository.VideoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    private final VideoRepository videoRepository;


    @RabbitListener(queues = "comment.queue")
    public void handleCommentCreated(CommentCreatedEvent event) {

        log.info("🗨️ New comment event received: videoId={}, commentId={}, authorId={}, text='{}'",
                event.getVideoId(), event.getCommentId(), event.getText(), event.getText());

        System.out.println("New comment event received: ");
        System.out.println("Video ID: " + event.getVideoId());
        System.out.println("Comment ID: " + event.getCommentId());
        System.out.println("Author ID: " + event.getAuthorId());
        System.out.println("Comment text: " + event.getText());
        System.out.println("Сохранено в базу даних: " + event.getCreatedAt());

        videoRepository.findById(event.getVideoId()).ifPresentOrElse(video -> {
            video.setComments(video.getComments() + 1);
            videoRepository.save(video);
            log.info("✅ Updated video {} with new comment count: {}", video.getId(), video.getComments());
        }, () -> {
            log.warn("⚠️ Video not found with ID: {}", event.getVideoId());
        });
    }
}

