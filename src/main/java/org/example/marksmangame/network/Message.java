package org.example.marksmangame.network;

public record Message<T>(
        MessageType type,
        T payload
) {}
