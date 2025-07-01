package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.DTO.event.PollOptionDTO;

import java.util.UUID;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollCreatedEvent {
    private UUID pollId;
    private UUID videoId;
    private String question;
    private List<PollOptionDTO> options;
}

