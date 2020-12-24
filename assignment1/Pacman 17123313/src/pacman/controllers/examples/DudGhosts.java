// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.controllers.Controller;

public class DudGhosts extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>
{
    @Override
    public EnumMap<Constants.GHOST, Constants.MOVE> getMove(final Game game, final long timeDue) {
        return null;
    }
}
