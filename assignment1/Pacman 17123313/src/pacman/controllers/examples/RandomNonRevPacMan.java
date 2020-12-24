// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples;

import pacman.game.Constants;
import pacman.game.Game;
import java.util.Random;
import pacman.controllers.PacmanController;

public final class RandomNonRevPacMan extends PacmanController
{
    Random rnd;
    
    public RandomNonRevPacMan() {
        this.rnd = new Random();
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        final Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
        return possibleMoves[this.rnd.nextInt(possibleMoves.length)];
    }
}
