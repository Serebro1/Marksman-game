package org.example.marksmangame.dto;

import java.io.Serializable;


public record CommandDTO(
        CommandType type,
        String playerName
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
