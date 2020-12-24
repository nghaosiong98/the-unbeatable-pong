// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.GameView;
import java.awt.Color;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.controllers.PacmanController;

public final class NearestPillPacManVS extends PacmanController
{
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        final int currentNodeIndex = game.getPacmanCurrentNodeIndex();
        final int[] activePills = game.getActivePillsIndices();
        final int[] activePowerPills = game.getActivePowerPillsIndices();
        final int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];
        for (int i = 0; i < activePills.length; ++i) {
            targetNodeIndices[i] = activePills[i];
        }
        for (int i = 0; i < activePowerPills.length; ++i) {
            targetNodeIndices[activePills.length + i] = activePowerPills[i];
        }
        final int nearest = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, targetNodeIndices, Constants.DM.PATH);
        if (game.getGhostLairTime(Constants.GHOST.BLINKY) == 0 && activePowerPills.length > 0) {
            GameView.addPoints(game, Color.RED, game.getAStarPath(game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY), activePowerPills[0], Constants.MOVE.NEUTRAL));
            GameView.addPoints(game, Color.YELLOW, game.getAStarPath(game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY), activePowerPills[0], game.getGhostLastMoveMade(Constants.GHOST.BLINKY)));
        }
        return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), nearest, Constants.DM.PATH);
    }
}
