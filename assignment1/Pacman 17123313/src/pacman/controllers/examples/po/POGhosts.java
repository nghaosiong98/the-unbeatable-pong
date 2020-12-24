// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples.po;

import java.util.Iterator;
import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class POGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    EnumMap<Constants.GHOST, POGhost> ghosts;
    EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    
    public POGhosts() {
        this.ghosts = new EnumMap<Constants.GHOST, POGhost>(Constants.GHOST.class);
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.ghosts.put(Constants.GHOST.BLINKY, new POGhost(Constants.GHOST.BLINKY));
        this.ghosts.put(Constants.GHOST.INKY, new POGhost(Constants.GHOST.INKY));
        this.ghosts.put(Constants.GHOST.PINKY, new POGhost(Constants.GHOST.PINKY));
        this.ghosts.put(Constants.GHOST.SUE, new POGhost(Constants.GHOST.SUE));
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        for (final Constants.GHOST ghost : this.ghosts.keySet()) {
            final Constants.MOVE move = this.ghosts.get(ghost).getMove(game.copy(ghost), timeDue);
            if (move != null) {
                this.myMoves.put(ghost, move);
            }
        }
        return this.myMoves;
    }
}
