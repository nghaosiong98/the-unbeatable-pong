// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;
import java.util.EnumMap;

public final class Node
{
    public final int x;
    public final int y;
    public final int nodeIndex;
    public final int pillIndex;
    public final int powerPillIndex;
    public final int numNeighbouringNodes;
    public final EnumMap<Constants.MOVE, Integer> neighbourhood;
    public EnumMap<Constants.MOVE, Constants.MOVE[]> allPossibleMoves;
    public EnumMap<Constants.MOVE, int[]> allNeighbouringNodes;
    public EnumMap<Constants.MOVE, EnumMap<Constants.MOVE, Integer>> allNeighbourhoods;
    
    public Node(final int nodeIndex, final int x, final int y, final int pillIndex, final int powerPillIndex, final int[] _neighbourhood) {
        this.neighbourhood = new EnumMap<Constants.MOVE, Integer>(Constants.MOVE.class);
        this.allPossibleMoves = new EnumMap<Constants.MOVE, Constants.MOVE[]>(Constants.MOVE.class);
        this.allNeighbouringNodes = new EnumMap<Constants.MOVE, int[]>(Constants.MOVE.class);
        this.allNeighbourhoods = new EnumMap<Constants.MOVE, EnumMap<Constants.MOVE, Integer>>(Constants.MOVE.class);
        this.nodeIndex = nodeIndex;
        this.x = x;
        this.y = y;
        this.pillIndex = pillIndex;
        this.powerPillIndex = powerPillIndex;
        final Constants.MOVE[] moves = Constants.MOVE.values();
        for (int i = 0; i < _neighbourhood.length; ++i) {
            if (_neighbourhood[i] != -1) {
                this.neighbourhood.put(moves[i], _neighbourhood[i]);
            }
        }
        this.numNeighbouringNodes = this.neighbourhood.size();
        for (int i = 0; i < moves.length; ++i) {
            if (this.neighbourhood.containsKey(moves[i])) {
                final EnumMap<Constants.MOVE, Integer> tmp = new EnumMap<Constants.MOVE, Integer>(this.neighbourhood);
                tmp.remove(moves[i]);
                this.allNeighbourhoods.put(moves[i].opposite(), tmp);
            }
        }
        this.allNeighbourhoods.put(Constants.MOVE.NEUTRAL, this.neighbourhood);
        final int[] neighbouringNodes = new int[this.numNeighbouringNodes];
        final Constants.MOVE[] possibleMoves = new Constants.MOVE[this.numNeighbouringNodes];
        int index = 0;
        for (int j = 0; j < moves.length; ++j) {
            if (this.neighbourhood.containsKey(moves[j])) {
                neighbouringNodes[index] = this.neighbourhood.get(moves[j]);
                possibleMoves[index] = moves[j];
                ++index;
            }
        }
        for (int j = 0; j < moves.length; ++j) {
            if (this.neighbourhood.containsKey(moves[j].opposite())) {
                final int[] tmpNeighbouringNodes = new int[this.numNeighbouringNodes - 1];
                final Constants.MOVE[] tmpPossibleMoves = new Constants.MOVE[this.numNeighbouringNodes - 1];
                index = 0;
                for (int k = 0; k < moves.length; ++k) {
                    if (moves[k] != moves[j].opposite() && this.neighbourhood.containsKey(moves[k])) {
                        tmpNeighbouringNodes[index] = this.neighbourhood.get(moves[k]);
                        tmpPossibleMoves[index] = moves[k];
                        ++index;
                    }
                }
                this.allNeighbouringNodes.put(moves[j], tmpNeighbouringNodes);
                this.allPossibleMoves.put(moves[j], tmpPossibleMoves);
            }
        }
        this.allNeighbouringNodes.put(Constants.MOVE.NEUTRAL, neighbouringNodes);
        this.allPossibleMoves.put(Constants.MOVE.NEUTRAL, possibleMoves);
    }
}
