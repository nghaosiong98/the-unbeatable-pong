// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import java.util.Arrays;
import pacman.game.Constants;

class JunctionData
{
    public int nodeID;
    public int nodeStartedFrom;
    public Constants.MOVE firstMove;
    public Constants.MOVE lastMove;
    public int[] path;
    public int[] reversePath;
    
    public JunctionData(final int nodeID, final Constants.MOVE firstMove, final int nodeStartedFrom, final int[] path, final Constants.MOVE lastMove) {
        this.nodeID = nodeID;
        this.nodeStartedFrom = nodeStartedFrom;
        this.firstMove = firstMove;
        this.path = path;
        this.lastMove = lastMove;
        if (path.length > 0) {
            this.reversePath = this.getReversePath(path);
        }
        else {
            this.reversePath = new int[0];
        }
    }
    
    public int[] getReversePath(final int[] path) {
        final int[] reversePath = new int[path.length];
        for (int i = 1; i < reversePath.length; ++i) {
            reversePath[i - 1] = path[path.length - 1 - i];
        }
        reversePath[reversePath.length - 1] = this.nodeStartedFrom;
        return reversePath;
    }
    
    @Override
    public String toString() {
        return this.nodeID + "\t" + this.firstMove.toString() + "\t" + Arrays.toString(this.path);
    }
}
