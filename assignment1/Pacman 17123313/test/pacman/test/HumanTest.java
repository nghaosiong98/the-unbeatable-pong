// 
// Decompiled by Procyon v0.5.36
// 

package pacman.test;

import java.util.EnumMap;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.internal.POType;
import java.awt.event.KeyListener;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POPacMan;
import pacman.controllers.HumanController;
import pacman.game.GameView;
import pacman.game.Game;
import pacman.controllers.KeyBoardInput;
import pacman.Executor;

public class HumanTest
{
    public static void main(final String[] args) {
        final Executor executor = new Executor(true, true);
        final KeyBoardInput input = new KeyBoardInput();
        final Game primaryGame = new Game(0L);
        final GameView view = new GameView(primaryGame).showGame();
        final GameView radiusView = new GameView(primaryGame).showGame();
        final GameView ff_losView = new GameView(primaryGame).showGame();
        view.setPO(true);
        radiusView.setPO(true);
        ff_losView.setPO(true);
        final HumanController human = new HumanController(input);
        final PacmanController pacman = new POPacMan();
        final POCommGhosts ghosts = new POCommGhosts(50);
        view.setFocusable(true);
        view.requestFocus();
        view.addKeyListener(human.getKeyboardInput());
        while (!primaryGame.gameOver()) {
            try {
                Thread.sleep(40L);
            }
            catch (Exception ex) {}
            primaryGame.PO_TYPE = POType.LOS;
            primaryGame.SIGHT_LIMIT = 100;
            final Constants.MOVE pacmanMove = pacman.getMove(primaryGame.copy(5), 40L);
            final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves = ghosts.getMove(primaryGame.copy(), -1L);
            primaryGame.advanceGame(pacmanMove, ghostMoves);
            view.paintImmediately(view.getBounds());
            primaryGame.PO_TYPE = POType.RADIUS;
            primaryGame.SIGHT_LIMIT = 45;
            radiusView.paintImmediately(radiusView.getBounds());
            primaryGame.PO_TYPE = POType.FF_LOS;
            primaryGame.SIGHT_LIMIT = 100;
            ff_losView.paintImmediately(ff_losView.getBounds());
        }
        System.out.println("Ended");
    }
}
