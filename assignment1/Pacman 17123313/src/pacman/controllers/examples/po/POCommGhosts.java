// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples.po;

import java.util.Iterator;
import java.util.Map;
import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class POCommGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private EnumMap<Constants.GHOST, POCommGhostImproved> ghosts;
    private EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    
    public POCommGhosts() {
        this(50);
    }
    
    public POCommGhosts(final int TICK_THRESHOLD) {
        this.ghosts = new EnumMap<Constants.GHOST, POCommGhostImproved>(Constants.GHOST.class);
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.ghosts.put(Constants.GHOST.BLINKY, new POCommGhostImproved(Constants.GHOST.BLINKY, TICK_THRESHOLD));
        this.ghosts.put(Constants.GHOST.INKY, new POCommGhostImproved(Constants.GHOST.INKY, TICK_THRESHOLD));
        this.ghosts.put(Constants.GHOST.PINKY, new POCommGhostImproved(Constants.GHOST.PINKY, TICK_THRESHOLD));
        this.ghosts.put(Constants.GHOST.SUE, new POCommGhostImproved(Constants.GHOST.SUE, TICK_THRESHOLD));

    }




    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        for (final Map.Entry<Constants.GHOST, POCommGhostImproved> entry : this.ghosts.entrySet()) {
            final Constants.MOVE move = entry.getValue().getMove(game.copy(entry.getKey()), timeDue);
            if (move != null) {
                this.myMoves.put(entry.getKey(), move);
            }
        }
        return this.myMoves;
    }
}
