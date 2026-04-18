package org.example.marksmangame.server.network;

public record Message<T>(
        MessageType type,
        T payload
) {}
