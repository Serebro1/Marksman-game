package org.example.marksmangame.dto;

import java.io.Serializable;

public class CommandDTO implements Serializable {
    private CommandType type;
    private String playerName;

    public CommandDTO(CommandType type, String playerName) {
        this.type = type;
        this.playerName = playerName;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return "CommandDTO{" +
                "type=" + type +
                ", playerName='" + playerName + '\'' +
                '}';
    }
}
