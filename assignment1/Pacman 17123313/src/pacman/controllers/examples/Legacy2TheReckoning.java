// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class Legacy2TheReckoning extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    public static final int CROWDED_DISTANCE = 30;
    public static final int PACMAN_DISTANCE = 10;
    public static final int PILL_PROXIMITY = 15;
    private final EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    private final EnumMap<Constants.GHOST, Integer> cornerAllocation;
    
    public Legacy2TheReckoning() {
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        (this.cornerAllocation = new EnumMap<Constants.GHOST, Integer>(Constants.GHOST.class)).put(Constants.GHOST.BLINKY, 0);
        this.cornerAllocation.put(Constants.GHOST.INKY, 1);
        this.cornerAllocation.put(Constants.GHOST.PINKY, 2);
        this.cornerAllocation.put(Constants.GHOST.SUE, 3);
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        final int pacmanIndex = game.getPacmanCurrentNodeIndex();
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                final int currentIndex = game.getGhostCurrentNodeIndex(ghost);
                if (this.isCrowded(game) && !this.closeToMsPacMan(game, currentIndex)) {
                    this.myMoves.put(ghost, this.getRetreatActions(game, ghost));
                }
                else if (game.getGhostEdibleTime(ghost) > 0 || this.closeToPower(game)) {
                    this.myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost), Constants.DM.PATH));
                }
                else {
                    this.myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost), Constants.DM.PATH));
                }
            }
        }
        return this.myMoves;
    }
    
    private boolean closeToPower(final Game game) {
        final int pacmanIndex = game.getPacmanCurrentNodeIndex();
        final int[] powerPillIndices = game.getActivePowerPillsIndices();
        for (int i = 0; i < powerPillIndices.length; ++i) {
            if (game.getShortestPathDistance(powerPillIndices[i], pacmanIndex) < 15) {
                return true;
            }
        }
        return false;
    }
    
    private boolean closeToMsPacMan(final Game game, final int location) {
        return game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), location) < 10;
    }
    
    private boolean isCrowded(final Game game) {
        final Constants.GHOST[] ghosts = Constants.GHOST.values();
        float distance = 0.0f;
        for (int i = 0; i < ghosts.length - 1; ++i) {
            for (int j = i + 1; j < ghosts.length; ++j) {
                distance += game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), game.getGhostCurrentNodeIndex(ghosts[j]));
            }
        }
        return distance / 6.0f < 30.0f;
    }
    
    private Constants.MOVE getRetreatActions(final Game game, final Constants.GHOST ghost) {
        final int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        final int pacManIndex = game.getPacmanCurrentNodeIndex();
        if (game.getGhostEdibleTime(ghost) == 0 && game.getShortestPathDistance(currentIndex, pacManIndex) < 10) {
            return game.getApproximateNextMoveTowardsTarget(currentIndex, pacManIndex, game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
        }
        return game.getApproximateNextMoveTowardsTarget(currentIndex, game.getPowerPillIndices()[this.cornerAllocation.get(ghost)], game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
    }
}
