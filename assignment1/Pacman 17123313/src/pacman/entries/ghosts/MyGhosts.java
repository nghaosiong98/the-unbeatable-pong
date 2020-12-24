// 
// Decompiled by Procyon v0.5.36
// 

package pacman.entries.ghosts;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class MyGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    
    public MyGhosts() {
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
    }
    
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        return this.myMoves;
    }
}
