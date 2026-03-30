package org.example.marksmangame.server.game;

import org.example.marksmangame.server.game.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerManager {
    private final List<Player> players = new ArrayList<>();

    public boolean addPlayer(Player player) {
        if (players.contains(player)) {
            return false;
        }
        players.add(player);
        return true;
    }

    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    public Optional<Player> findPlayer(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    public void setPlayerReady(String playerName, boolean ready) {
        findPlayer(playerName).ifPresent(p -> p.setReady(ready));
    }

    public boolean allPlayersReady() {
        return !players.isEmpty() && players.stream().allMatch(Player::isReady);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public int size() {
        return players.size();
    }
}
