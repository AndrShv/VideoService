package org.example.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.example.event.VideoCreatingEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
public class RabbitMqConfig {

    @Value("${queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.username}")
    private String userName;

    @Value("${spring.rabbitmq.password}")
    private String password;


    @Bean
    ConnectionFactory ConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        factory.setUsername(userName);
        factory.setPassword(password);

        return factory;
    }


    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public TopicExchange videoExchange() {
        return new TopicExchange("video.exchange");
    }

    @Bean
    public Queue videoCreateQueue() {
        return new Queue("video.create.queue", true);
    }


    @Bean
    public Binding bindVideoCreateQueue() {
        return BindingBuilder
                .bind(videoCreateQueue())
                .to(videoExchange())
                .with("video.create");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public TopicExchange reactionExchange() {
        return new TopicExchange("reaction.exchange");
    }

    @Bean
    public Queue reactionQueue() {
        return new Queue("reaction.queue", true);
    }

    @Bean
    public Binding reactionBinding() {
        return BindingBuilder
                .bind(reactionQueue())
                .to(reactionExchange())
                .with("reaction.create");
    }


    @Bean
    public TopicExchange pollExchange() {
        return new TopicExchange("poll.exchange");
    }

    @Bean
    public Queue pollCreatedQueue() {
        return new Queue("poll.created.queue");
    }

    @Bean
    public Binding pollCreatedBinding() {
        return BindingBuilder
                .bind(pollCreatedQueue())
                .to(pollExchange())
                .with("poll.created");
    }



    @Bean
    public TopicExchange voteExchange() {
        return new TopicExchange("vote.exchange");
    }


    @Bean
    public Queue voteCreatesQueue() {
        return new Queue("vote.create.queue");
    }

    @Bean
    public Binding pollVotedBinding() {
        return BindingBuilder
                .bind(voteCreatesQueue())
                .to(pollExchange())
                .with("vote.created");
    }




}
