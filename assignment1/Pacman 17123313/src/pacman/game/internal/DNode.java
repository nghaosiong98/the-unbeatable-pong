// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;
import java.util.ArrayList;

class DNode
{
    public int nodeID;
    public ArrayList<JunctionData> closestJunctions;
    public boolean isJunction;
    
    public DNode(final int nodeID, final boolean isJunction) {
        this.nodeID = nodeID;
        this.isJunction = isJunction;
        this.closestJunctions = new ArrayList<JunctionData>();
        if (isJunction) {
            this.closestJunctions.add(new JunctionData(nodeID, Constants.MOVE.NEUTRAL, nodeID, new int[0], Constants.MOVE.NEUTRAL));
        }
    }
    
    public int[] getPathToJunction(final Constants.MOVE lastMoveMade) {
        if (this.isJunction) {
            return new int[0];
        }
        for (int i = 0; i < this.closestJunctions.size(); ++i) {
            if (!this.closestJunctions.get(i).firstMove.equals(lastMoveMade.opposite())) {
                return this.closestJunctions.get(i).path;
            }
        }
        return null;
    }
    
    public JunctionData getNearestJunction(final Constants.MOVE lastMoveMade) {
        if (this.isJunction) {
            return this.closestJunctions.get(0);
        }
        int minDist = Integer.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < this.closestJunctions.size(); ++i) {
            if (!this.closestJunctions.get(i).firstMove.equals(lastMoveMade.opposite())) {
                final int newDist = this.closestJunctions.get(i).path.length;
                if (newDist < minDist) {
                    minDist = newDist;
                    bestIndex = i;
                }
            }
        }
        if (bestIndex != -1) {
            return this.closestJunctions.get(bestIndex);
        }
        return null;
    }
    
    public void addPath(final int junctionID, final Constants.MOVE firstMove, final int nodeStartedFrom, final int[] path, final Constants.MOVE lastMove) {
        this.closestJunctions.add(new JunctionData(junctionID, firstMove, nodeStartedFrom, path, lastMove));
    }
    
    @Override
    public String toString() {
        return "" + this.nodeID + "\t" + this.isJunction;
    }
}
