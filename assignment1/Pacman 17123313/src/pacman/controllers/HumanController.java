// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers;

import pacman.game.Constants;
import pacman.game.Game;

public class HumanController extends PacmanController
{
    public KeyBoardInput input;
    
    public HumanController(final KeyBoardInput input) {
        this.input = input;
    }
    
    public KeyBoardInput getKeyboardInput() {
        return this.input;
    }
    
    @Override
    public Constants.MOVE getMove(final Game game, final long dueTime) {
        switch (this.input.getKey()) {
            case 38: {
                return Constants.MOVE.UP;
            }
            case 39: {
                return Constants.MOVE.RIGHT;
            }
            case 40: {
                return Constants.MOVE.DOWN;
            }
            case 37: {
                return Constants.MOVE.LEFT;
            }
            default: {
                return Constants.MOVE.NEUTRAL;
            }
        }
    }
}
