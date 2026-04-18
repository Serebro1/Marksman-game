package org.example.marksmangame.dto;

public record CommandDTO(
        CommandType type,
        String playerName
) implements MessageDTO {}
