package org.example.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.event.VideoCreatingEvent;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MsgProducerToUserProfileService {
    private final RabbitTemplate rabbitTemplate;

    @Value("video.create.queue")
    private String queueName;

    public void sendVideoCreatedEvent(VideoCreatingEvent event) {
        rabbitTemplate.convertAndSend(queueName, event);
    }

}
