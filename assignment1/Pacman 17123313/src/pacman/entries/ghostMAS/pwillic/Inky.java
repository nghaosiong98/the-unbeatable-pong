// 
// Decompiled by Procyon v0.5.36
// 

package pacman.entries.ghostMAS.pwillic;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.IndividualGhostController;

public class Inky extends IndividualGhostController
{
    private POCommGhost poCommGhost;
    
    public Inky() {
        super(Constants.GHOST.INKY);
        this.poCommGhost = new POCommGhost(Constants.GHOST.INKY, 50);
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return null;
    }
}
