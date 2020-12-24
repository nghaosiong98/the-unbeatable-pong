// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples.po;

import java.util.ArrayList;
import pacman.game.Constants;
import pacman.game.Game;
import java.util.Random;
import pacman.controllers.PacmanController;

public class POPacMan extends PacmanController
{
    private static final int MIN_DISTANCE = 20;
    private Random random;
    
    public POPacMan() {
        this.random = new Random();
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        final int current = game.getPacmanCurrentNodeIndex();
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                final int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
                if (ghostLocation != -1 && game.getShortestPathDistance(current, ghostLocation) < 20) {
                    return game.getNextMoveAwayFromTarget(current, ghostLocation, Constants.DM.PATH);
                }
            }
        }
        int minDistance = Integer.MAX_VALUE;
        Constants.GHOST minGhost = null;
        for (final Constants.GHOST ghost2 : Constants.GHOST.values()) {
            if (game.getGhostEdibleTime(ghost2) > 0) {
                final int distance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost2));
                if (distance < minDistance) {
                    minDistance = distance;
                    minGhost = ghost2;
                }
            }
        }
        if (minGhost != null) {
            return game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(minGhost), Constants.DM.PATH);
        }
        final int[] pills = game.getPillIndices();
        final int[] powerPills = game.getPowerPillIndices();
        final ArrayList<Integer> targets = new ArrayList<Integer>();
        for (int i = 0; i < pills.length; ++i) {
            final Boolean pillStillAvailable = game.isPillStillAvailable(i);
            if (pillStillAvailable != null) {
                if (game.isPillStillAvailable(i)) {
                    targets.add(pills[i]);
                }
            }
        }
        for (int i = 0; i < powerPills.length; ++i) {
            final Boolean pillStillAvailable = game.isPillStillAvailable(i);
            if (pillStillAvailable != null) {
                if (game.isPowerPillStillAvailable(i)) {
                    targets.add(powerPills[i]);
                }
            }
        }
        if (!targets.isEmpty()) {
            final int[] targetsArray = new int[targets.size()];
            for (int j = 0; j < targetsArray.length; ++j) {
                targetsArray[j] = targets.get(j);
            }
            return game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(current, targetsArray, Constants.DM.PATH), Constants.DM.PATH);
        }
        final Constants.MOVE[] moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
        if (moves.length > 0) {
            return moves[this.random.nextInt(moves.length)];
        }
        return game.getPacmanLastMoveMade().opposite();
    }
}
