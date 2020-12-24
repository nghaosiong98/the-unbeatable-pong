// 
// Decompiled by Procyon v0.5.36
// 

package pacmanv3.entries.ghostMAS;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.IndividualGhostController;

public class Pinky extends IndividualGhostController
{
    public Pinky() {
        super(Constants.GHOST.PINKY);
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return null;
    }
}
