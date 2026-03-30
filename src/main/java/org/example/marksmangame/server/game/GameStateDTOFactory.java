package org.example.marksmangame.server.game;

import org.example.marksmangame.dto.ArrowDTO;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.dto.PlayerDTO;
import org.example.marksmangame.dto.TargetDTO;
import org.example.marksmangame.server.game.model.Arrow;
import org.example.marksmangame.server.game.model.Player;
import org.example.marksmangame.server.game.model.Target;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTOFactory {

    public static GameStateDTO create(PlayerManager playerManager,
                                      GameMechanics mechanics,
                                      GameStateController stateController) {
        // Преобразование мишеней
        List<TargetDTO> targetDTOs = new ArrayList<>();
        List<Target> targets = mechanics.getTargets();
        for (int i = 0; i < targets.size(); i++) {
            Target t = targets.get(i);
            targetDTOs.add(new TargetDTO(i, t.getX(), t.getY(), t.getRadius(), true));
        }

        // Преобразование стрел
        List<ArrowDTO> arrowDTOs = new ArrayList<>();
        List<Arrow> arrows = mechanics.getArrows();
        List<Player> players = playerManager.getPlayers();
        for (Arrow a : arrows) {
            if (!a.isActive()) continue;
            int ownerId = players.indexOf(a.getOwner());
            if (ownerId >= 0) {
                arrowDTOs.add(new ArrowDTO(a.getX(), a.getY(), ownerId, true));
            }
        }

        // Преобразование игроков
        List<PlayerDTO> playerDTOs = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            playerDTOs.add(new PlayerDTO(i, p.getName(), p.getScore(), p.getShots(), p.isReady()));
        }

        return new GameStateDTO(
                targetDTOs,
                arrowDTOs,
                playerDTOs,
                stateController.getState(),
                stateController.getPausedBy(),
                stateController.getWinnerName()
        );
    }
}
