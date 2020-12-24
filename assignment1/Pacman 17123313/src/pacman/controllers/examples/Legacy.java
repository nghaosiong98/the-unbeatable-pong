// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import java.util.Random;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class Legacy extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    Random rnd;
    EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    Constants.MOVE[] moves;
    
    public Legacy() {
        this.rnd = new Random();
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.moves = Constants.MOVE.values();
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        final int targetNode = game.getPacmanCurrentNodeIndex();
        if (game.doesGhostRequireAction(Constants.GHOST.BLINKY)) {
            this.myMoves.put(Constants.GHOST.BLINKY, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY), targetNode, game.getGhostLastMoveMade(Constants.GHOST.BLINKY), Constants.DM.PATH));
        }
        if (game.doesGhostRequireAction(Constants.GHOST.INKY)) {
            this.myMoves.put(Constants.GHOST.INKY, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(Constants.GHOST.INKY), targetNode, game.getGhostLastMoveMade(Constants.GHOST.INKY), Constants.DM.MANHATTAN));
        }
        if (game.doesGhostRequireAction(Constants.GHOST.PINKY)) {
            this.myMoves.put(Constants.GHOST.PINKY, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY), targetNode, game.getGhostLastMoveMade(Constants.GHOST.PINKY), Constants.DM.EUCLID));
        }
        if (game.doesGhostRequireAction(Constants.GHOST.SUE)) {
            this.myMoves.put(Constants.GHOST.SUE, this.moves[this.rnd.nextInt(this.moves.length)]);
        }
        return this.myMoves;
    }
}
