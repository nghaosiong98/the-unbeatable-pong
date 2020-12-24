// 
// Decompiled by Procyon v0.5.36
// 

package pacman.entries.ghostMAS.pwillic;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.IndividualGhostController;

public class Sue extends IndividualGhostController
{
    private POCommGhost poCommGhost;
    
    public Sue() {
        super(Constants.GHOST.SUE);
        this.poCommGhost = new POCommGhost(Constants.GHOST.SUE, 50);
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return this.poCommGhost.getMove(game, timeDue);
    }
}
