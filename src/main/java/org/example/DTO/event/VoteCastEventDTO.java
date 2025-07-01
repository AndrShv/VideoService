package org.example.DTO.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteCastEventDTO {
    private UUID userId;
    private UUID pollId;
    private UUID optionId;
}
