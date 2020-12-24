// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers;

import pacman.game.Game;
import pacman.game.Constants;

public abstract class IndividualGhostController
{
    protected final Constants.GHOST ghost;
    
    public IndividualGhostController(final Constants.GHOST ghost) {
        this.ghost = ghost;
    }
    
    public abstract Constants.MOVE getMove(final Game p0, final long p1);
}
