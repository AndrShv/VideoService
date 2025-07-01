package org.example.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteCastEvent{
    private UUID userId;
    private UUID pollId;
    private UUID optionId;
}
