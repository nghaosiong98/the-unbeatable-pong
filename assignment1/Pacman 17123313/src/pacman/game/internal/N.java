// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import pacman.game.Constants;
import java.util.ArrayList;

class N implements Comparable<N>
{
    public N parent;
    public double g;
    public double h;
    public boolean visited;
    public ArrayList<E> adj;
    public int index;
    public Constants.MOVE reached;
    
    public N(final int index) {
        this.visited = false;
        this.reached = null;
        this.adj = new ArrayList<E>();
        this.index = index;
    }
    
    public N(final double g, final double h) {
        this.visited = false;
        this.reached = null;
        this.g = g;
        this.h = h;
    }
    
    public boolean isEqual(final N another) {
        return this.index == another.index;
    }
    
    @Override
    public String toString() {
        return "" + this.index;
    }
    
    @Override
    public int compareTo(final N another) {
        if (this.g + this.h < another.g + another.h) {
            return -1;
        }
        if (this.g + this.h > another.g + another.h) {
            return 1;
        }
        return 0;
    }
}
