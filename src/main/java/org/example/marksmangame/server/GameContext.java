package org.example.marksmangame.server;

import org.example.marksmangame.db.GameService;

public record GameContext (
        Engine engine,
        GameService service,
        GameServer server
) {}
