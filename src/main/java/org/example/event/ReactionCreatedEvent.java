package org.example.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.reactions.Reactions;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionCreatedEvent {
    private UUID reactionId;
    private UUID videoId;
    private UUID authorId;
    private Reactions type;
}

