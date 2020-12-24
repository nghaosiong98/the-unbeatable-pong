// 
// Decompiled by Procyon v0.5.36
// 

package pacman.test;

import java.util.EnumMap;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POPacMan;
import pacman.game.Constants;
import pacman.game.GameView;
import pacman.game.Game;

public class GhostViewTest
{
    public static void main(final String[] args) {
        final Game primaryGame = new Game(0L);
        final GameView primaryView = new GameView(primaryGame).showGame();
        final GameView[] ghostViews = { new GameView(primaryGame).showGame(), new GameView(primaryGame).showGame(), new GameView(primaryGame).showGame(), new GameView(primaryGame).showGame() };
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            ghostViews[ghost.ordinal()].setPO(true, ghost);
        }
        final PacmanController pacman = new POPacMan();
        final POCommGhosts ghosts = new POCommGhosts(50);
        while (!primaryGame.gameOver()) {
            try {
                Thread.sleep(40L);
            }
            catch (Exception ex) {}
            final Constants.MOVE pacmanMove = pacman.getMove(primaryGame.copy(5), 40L);
            final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves = ghosts.getMove(primaryGame.copy(), -1L);
            primaryGame.advanceGame(pacmanMove, ghostMoves);
            primaryView.paintImmediately(primaryView.getBounds());
            for (final GameView view : ghostViews) {
                view.paintImmediately(view.getBounds());
            }
        }
    }
}
