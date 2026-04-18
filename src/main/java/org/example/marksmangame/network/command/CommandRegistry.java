package org.example.marksmangame.server.network.command;

import org.example.marksmangame.dto.CommandType;

import java.util.EnumMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<CommandType, Command> commands = new EnumMap<>(CommandType.class);

    public CommandRegistry() {
        commands.put(CommandType.CONNECT, new ConnectCommand());
        commands.put(CommandType.READY, new ReadyCommand());
        commands.put(CommandType.PAUSE, new PauseCommand());
        commands.put(CommandType.RESUME, new ResumeCommand());
        commands.put(CommandType.SHOOT, new ShootCommand());
        commands.put(CommandType.STOP, new StopCommand());
        commands.put(CommandType.DISCONNECT, new DisconnectCommand());
        commands.put(CommandType.LEADERBOARD, new LeaderboardCommand());
        commands.put(CommandType.HISTORY, new HistoryCommand());
    }

    public Command getCommand(CommandType type) {
        return commands.get(type);
    }
}
