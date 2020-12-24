// 
// Decompiled by Procyon v0.5.36
// 

package pacman.entries.pacman;

import pacman.game.Game;
import pacman.game.Constants;
import pacman.controllers.PacmanController;

public class MyPacMan extends PacmanController
{
    private Constants.MOVE myMove;
    
    public MyPacMan() {
        this.myMove = Constants.MOVE.NEUTRAL;
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return this.myMove;
    }
}
