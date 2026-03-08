package org.example.marksmangame.dto;

import java.io.Serializable;
import java.util.List;

public class GameStateDTO implements Serializable {
    private List<TargetDTO> targets;
    private List<ArrowDTO> arrows;
    private List<PlayerDTO> players;
    private GameState state;
    private String winnerName;

    public GameStateDTO(List<TargetDTO> targets, List<ArrowDTO> arrows, List<PlayerDTO> players, GameState state, String winnerName) {
        this.targets = targets;
        this.arrows = arrows;
        this.players = players;
        this.state = state;
        this.winnerName = winnerName;
    }

    public List<TargetDTO> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetDTO> targets) {
        this.targets = targets;
    }

    public List<ArrowDTO> getArrows() {
        return arrows;
    }

    public void setArrows(List<ArrowDTO> arrows) {
        this.arrows = arrows;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    @Override
    public String toString() {
        return "GameStateDTO{" +
                "targets=" + targets +
                ", arrows=" + arrows +
                ", players=" + players +
                ", state=" + state +
                ", winnerName='" + winnerName + '\'' +
                '}';
    }
}
