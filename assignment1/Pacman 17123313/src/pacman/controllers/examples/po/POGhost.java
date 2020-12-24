// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples.po;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.Random;

class POGhost
{
    private static final float CONSISTENCY = 0.9f;
    private static final int PILL_PROXIMITY = 15;
    Random rnd;
    private Constants.GHOST ghost;
    
    public POGhost(final Constants.GHOST ghost) {
        this.rnd = new Random();
        this.ghost = ghost;
    }
    
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        if (!game.doesGhostRequireAction(this.ghost)) {
            return null;
        }
        if (game.getGhostEdibleTime(this.ghost) > 0 || this.closeToPower(game)) {
            return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(this.ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
        }
        if (game.getPacmanCurrentNodeIndex() != -1 && this.rnd.nextFloat() < 0.9f) {
            final Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(this.ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
            return move;
        }
        final Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(this.ghost), game.getGhostLastMoveMade(this.ghost));
        return possibleMoves[this.rnd.nextInt(possibleMoves.length)];
    }
    
    private boolean closeToPower(final Game game) {
        final int[] powerPills = game.getPowerPillIndices();
        for (int i = 0; i < powerPills.length; ++i) {
            final Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            final int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < 15) {
                return true;
            }
        }
        return false;
    }
}
