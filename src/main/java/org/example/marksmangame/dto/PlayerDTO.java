package org.example.marksmangame.dto;

public record PlayerDTO(
        int id,
        String name,
        int score,
        int shots,
        boolean ready
) implements MessageDTO {}
