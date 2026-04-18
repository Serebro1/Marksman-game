package org.example.marksmangame.dto;

public record ArrowDTO(
        double x,
        double y,
        int playerId,
        boolean active
) implements MessageDTO {}
