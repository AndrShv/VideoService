package org.example.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "polls")
@Getter
@Setter
public class Polls {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @OneToMany(mappedBy = "polls", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> options;

    public Polls() {
    }
}

