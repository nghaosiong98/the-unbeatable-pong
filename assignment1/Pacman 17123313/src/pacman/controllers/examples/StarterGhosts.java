// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import java.util.Random;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public final class StarterGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private static final float CONSISTENCY = 0.9f;
    private static final int PILL_PROXIMITY = 15;
    Random rnd;
    EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    
    public StarterGhosts() {
        this.rnd = new Random();
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                if (game.getGhostEdibleTime(ghost) > 0 || this.closeToPower(game)) {
                    this.myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH));
                }
                else if (this.rnd.nextFloat() < 0.9f) {
                    this.myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH));
                }
                else {
                    final Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                    this.myMoves.put(ghost, possibleMoves[this.rnd.nextInt(possibleMoves.length)]);
                }
            }
        }
        return this.myMoves;
    }
    
    private boolean closeToPower(final Game game) {
        final int[] powerPills = game.getPowerPillIndices();
        for (int i = 0; i < powerPills.length; ++i) {
            if (game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i], game.getPacmanCurrentNodeIndex()) < 15) {
                return true;
            }
        }
        return false;
    }
}
