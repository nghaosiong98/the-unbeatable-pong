// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;

public final class MASController extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    private EnumMap<Constants.GHOST, Constants.MOVE> myMoves;
    private EnumMap<Constants.GHOST, IndividualGhostController> controllers;
    
    public MASController(final EnumMap<Constants.GHOST, IndividualGhostController> controllers) {
        this.myMoves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
        this.controllers = new EnumMap<Constants.GHOST, IndividualGhostController>(Constants.GHOST.class);
        this.controllers = controllers;
    }
    
    @Override
    public final EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        this.myMoves.clear();
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            this.myMoves.put(ghost, this.controllers.get(ghost).getMove(game.copy(ghost), timeDue));
        }
        return this.myMoves;
    }
}
