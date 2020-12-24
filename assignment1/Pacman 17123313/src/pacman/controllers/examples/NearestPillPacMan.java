// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.controllers.PacmanController;

public class NearestPillPacMan extends PacmanController
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
        return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, targetNodeIndices, Constants.DM.PATH), Constants.DM.PATH);
    }
}
