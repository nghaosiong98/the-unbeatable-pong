// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Game;
import pacman.game.Constants;
import java.util.Random;
import pacman.controllers.PacmanController;

public final class RandomPacMan extends PacmanController
{
    private Random rnd;
    private Constants.MOVE[] allMoves;
    
    public RandomPacMan() {
        this.rnd = new Random();
        this.allMoves = Constants.MOVE.values();
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        return this.allMoves[this.rnd.nextInt(this.allMoves.length)];
    }
}
