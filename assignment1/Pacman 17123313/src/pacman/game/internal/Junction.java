// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;
import java.util.EnumMap;

class Junction
{
    public int jctId;
    public int nodeId;
    public EnumMap<Constants.MOVE, int[]>[] paths;
    
    public Junction(final int jctId, final int nodeId, final int numJcts) {
        this.jctId = jctId;
        this.nodeId = nodeId;
        this.paths = (EnumMap<Constants.MOVE, int[]>[])new EnumMap[numJcts];
        for (int i = 0; i < this.paths.length; ++i) {
            this.paths[i] = new EnumMap<Constants.MOVE, int[]>(Constants.MOVE.class);
        }
    }
    
    public void computeShortestPaths() {
        final Constants.MOVE[] moves = Constants.MOVE.values();
        for (int i = 0; i < this.paths.length; ++i) {
            if (i == this.jctId) {
                this.paths[i].put(Constants.MOVE.NEUTRAL, new int[0]);
            }
            else {
                int distance = Integer.MAX_VALUE;
                int[] path = null;
                for (int j = 0; j < moves.length; ++j) {
                    if (this.paths[i].containsKey(moves[j])) {
                        final int[] tmp = this.paths[i].get(moves[j]);
                        if (tmp.length < distance) {
                            distance = tmp.length;
                            path = tmp;
                        }
                    }
                }
                this.paths[i].put(Constants.MOVE.NEUTRAL, path);
            }
        }
    }
    
    public void addPath(final int toJunction, final Constants.MOVE firstMoveMade, final int[] path) {
        this.paths[toJunction].put(firstMoveMade, path);
    }
    
    @Override
    public String toString() {
        return this.jctId + "\t" + this.nodeId;
    }
}
