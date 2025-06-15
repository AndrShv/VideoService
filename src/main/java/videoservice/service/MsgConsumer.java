package videoservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import videoservice.entity.Video;
import videoservice.event.VideoCreatingEvent;
import videoservice.repository.VideoRepository;

import java.sql.SQLOutput;

@Service
public class MsgConsumer {

    @Autowired
    private VideoRepository videoRepository;

    @RabbitListener(queues = "video.create.queue")
    public void handleVideoCreate(VideoCreatingEvent event) {
        System.out.println("Получено событие: " + event);
        Video video = new Video();
        video.setAuthorId(event.getAuthorId());
        video.setTitle(event.getTitle());
        video.setVideoUrl(event.getVideoUrl());
        video.setDescription(event.getDescription());
        video.setDuration(60L);
        video.setThumbnailUrl("https://example.com/thumb.jpg");
        video.setCategory("Welcome");

        videoRepository.save(video);
        System.out.println("Video created: " + video.getTitle());
        System.out.println("Video created: "+ video.getId());
    }

}
