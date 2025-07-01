package org.example.DTO.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollCreatedEventDTO {
    private UUID pollId;
    private UUID videoId;
    private String question;
    private List<PollOptionDTO> options;
}
