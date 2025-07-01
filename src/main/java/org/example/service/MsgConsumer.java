package org.example.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.event.VideoCreatingEvent;
import org.example.repository.VideoRepository;

@Service
public class MsgConsumer {

    @Autowired
    private VideoRepository videoRepository;

    @RabbitListener(queues = "video.create.queue")
    public void handleVideoCreate(VideoCreatingEvent event) {
        System.out.println("Получено событие: " + event);
        System.out.println(event.getAuthorId());
        System.out.println(event.getTitle());
        System.out.println(event.getDescription());
        System.out.println(event.getVideoUrl());

    }
}
