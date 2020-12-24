// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;

class E
{
    public N node;
    public Constants.MOVE move;
    public double cost;
    
    public E(final N node, final Constants.MOVE move, final double cost) {
        this.node = node;
        this.move = move;
        this.cost = cost;
    }
}
