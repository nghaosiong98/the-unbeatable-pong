// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;

public final class Ghost
{
    public int currentNodeIndex;
    public int edibleTime;
    public int lairTime;
    public Constants.GHOST type;
    public Constants.MOVE lastMoveMade;
    
    public Ghost(final Constants.GHOST type, final int currentNodeIndex, final int edibleTime, final int lairTime, final Constants.MOVE lastMoveMade) {
        this.type = type;
        this.currentNodeIndex = currentNodeIndex;
        this.edibleTime = edibleTime;
        this.lairTime = lairTime;
        this.lastMoveMade = lastMoveMade;
    }
    
    public Ghost copy() {
        return new Ghost(this.type, this.currentNodeIndex, this.edibleTime, this.lairTime, this.lastMoveMade);
    }
}
