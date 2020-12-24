// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;

public final class PacMan
{
    public int currentNodeIndex;
    public int numberOfLivesRemaining;
    public Constants.MOVE lastMoveMade;
    public boolean hasReceivedExtraLife;
    
    public PacMan(final int currentNodeIndex, final Constants.MOVE lastMoveMade, final int numberOfLivesRemaining, final boolean hasReceivedExtraLife) {
        this.currentNodeIndex = currentNodeIndex;
        this.lastMoveMade = lastMoveMade;
        this.numberOfLivesRemaining = numberOfLivesRemaining;
        this.hasReceivedExtraLife = hasReceivedExtraLife;
    }
    
    public PacMan copy() {
        return new PacMan(this.currentNodeIndex, this.lastMoveMade, this.numberOfLivesRemaining, this.hasReceivedExtraLife);
    }
}
