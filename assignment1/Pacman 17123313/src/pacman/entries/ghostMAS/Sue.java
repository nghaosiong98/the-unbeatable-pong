// 
// Decompiled by Procyon v0.5.36
// 

package pacmanv3.entries.ghostMAS;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.IndividualGhostController;

public class Sue extends IndividualGhostController
{
    public Sue() {
        super(Constants.GHOST.SUE);
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return null;
    }
}
