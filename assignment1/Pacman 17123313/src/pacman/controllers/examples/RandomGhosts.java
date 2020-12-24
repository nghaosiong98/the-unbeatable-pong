// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import java.util.Random;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public final class RandomGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private EnumMap<Constants.GHOST, Constants.MOVE> moves;
    private Constants.MOVE[] allMoves;
    private Random rnd;
    
    public RandomGhosts() {
        this.moves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.allMoves = Constants.MOVE.values();
        this.rnd = new Random();
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.moves.clear();
        for (final Constants.GHOST ghostType : Constants.GHOST.values()) {
            if (game.doesGhostRequireAction(ghostType)) {
                this.moves.put(ghostType, this.allMoves[this.rnd.nextInt(this.allMoves.length)]);
            }
        }
        return this.moves;
    }
}
