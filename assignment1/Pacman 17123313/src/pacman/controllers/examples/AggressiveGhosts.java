// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import java.util.Random;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public final class AggressiveGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private static final float CONSISTENCY = 1.0f;
    private Random rnd;
    private EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    private Constants.MOVE[] moves;
    
    public AggressiveGhosts() {
        this.rnd = new Random();
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.moves = Constants.MOVE.values();
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                if (this.rnd.nextFloat() < 1.0f) {
                    this.myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH));
                }
                else {
                    this.myMoves.put(ghost, this.moves[this.rnd.nextInt(this.moves.length)]);
                }
            }
        }
        return this.myMoves;
    }
}
