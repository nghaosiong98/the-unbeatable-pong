// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.info;

import pacman.game.internal.Ghost;
import pacman.game.Constants;
import java.util.EnumMap;
import pacman.game.internal.PacMan;
import java.util.BitSet;

public class GameInfo
{
    private BitSet pills;
    private BitSet powerPills;
    private PacMan pacman;
    private EnumMap<Constants.GHOST, Ghost> ghosts;
    
    public GameInfo(final int pillsLength) {
        this.ghosts = new EnumMap<Constants.GHOST, Ghost>(Constants.GHOST.class);
        this.pills = new BitSet(pillsLength);
        this.powerPills = new BitSet(4);
    }
    
    public void setPillAtIndex(final int index, final boolean value) {
        this.pills.set(index, value);
    }
    
    public void setPowerPillAtIndex(final int index, final boolean value) {
        this.powerPills.set(index, value);
    }
    
    public void setGhostIndex(final Constants.GHOST ghost, final Ghost data) {
        this.ghosts.put(ghost, data);
    }
    
    public BitSet getPills() {
        return this.pills;
    }
    
    public BitSet getPowerPills() {
        return this.powerPills;
    }
    
    public EnumMap<Constants.GHOST, Ghost> getGhosts() {
        return this.ghosts;
    }
    
    public PacMan getPacman() {
        return this.pacman;
    }
    
    public void setPacman(final PacMan pacman) {
        this.pacman = pacman;
    }
    public void fixGhosts(){
        
    }
}
