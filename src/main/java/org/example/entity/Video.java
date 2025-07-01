package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Column(name = "video_url", nullable = false, unique = true)
    private String videoUrl;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "duration", nullable = false)
    private Long duration;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "likes", nullable = false)
    private Long likes = 0L;

    @Column(name = "dislikes", nullable = false)
    private Long dislikes = 0L;

    @Column(name = "laughs", nullable = false)
    private Long laughs = 0L;

    @Column(name = "cries", nullable = false)
    private Long cries = 0L;

    @Column(name = "angries", nullable = false)
    private Long angries = 0L;


    @Column(name = "comments", nullable = false)
    private Long comments = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Polls> polls;


    public Video() {}
}
