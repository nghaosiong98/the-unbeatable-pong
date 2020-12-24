// 
// Decompiled by Procyon v0.5.36
// 

package pacmanv3.entries.ghostMAS;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.IndividualGhostController;

public class Blinky extends IndividualGhostController
{
    public Blinky() {
        super(Constants.GHOST.BLINKY);
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return null;
    }
}
