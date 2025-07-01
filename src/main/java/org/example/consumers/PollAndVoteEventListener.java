package org.example.consumers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.PollCreatedEvent;
import org.example.repository.VideoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PollAndVoteEventListener {
    private final VideoRepository videoRepository;


    @RabbitListener(queues = "poll.created.queue")
    public void handlePollCreatedEvent(PollCreatedEvent event) {
        log.info("📊 New poll created: videoId={}, pollId={}, question='{}'",
                event.getVideoId(), event.getPollId(), event.getQuestion());

        System.out.println("New poll created: ");
        System.out.println("Video ID: " + event.getVideoId());
        System.out.println("Poll ID: " + event.getPollId());
        System.out.println("Question: " + event.getQuestion());

        videoRepository.findById(event.getVideoId()).ifPresentOrElse(video -> {
            video.setPolls(video.getPolls());
            videoRepository.save(video);
            log.info("✅ Updated video {} with new poll: {}", video.getId(), video.getPolls());
        }, () -> {
            log.warn("⚠️ Video not found with ID: {}", event.getVideoId());
        });
    }

}
