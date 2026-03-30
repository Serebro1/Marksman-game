package org.example.marksmangame.server.network;

import org.example.marksmangame.server.db.GameService;
import org.example.marksmangame.server.game.Engine;

public record GameContext (
        Engine engine,
        GameService service,
        GameServer server
) {}
