package org.example.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Video;
import org.example.event.ReactionCreatedEvent;
import org.example.repository.VideoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactionEventListener {

    private final VideoRepository videoRepository;

    @RabbitListener(queues = "reaction.queue")
    public void handleReactionCreated(ReactionCreatedEvent event) {
        log.info("📢 New reaction: {} on video {}", event.getType(), event.getVideoId());

        videoRepository.findById(event.getVideoId()).ifPresent(video -> {
            switch (event.getType()) {
                case LIKE -> video.setLikes(video.getLikes() + 1);
                case DISLIKE -> video.setDislikes(video.getDislikes() + 1);
                case LAUGH -> video.setLaughs(video.getLaughs() + 1);
                case CRY -> video.setCries(video.getCries() + 1);
                case ANGRY -> video.setAngries(video.getAngries() + 1);
            }

            videoRepository.save(video);
            log.info("✅ Reaction {} counted for video {}", event.getType(), video.getId());


        });
    }

}
