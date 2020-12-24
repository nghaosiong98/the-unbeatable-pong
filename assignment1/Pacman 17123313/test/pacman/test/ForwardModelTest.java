// 
// Decompiled by Procyon v0.5.36
// 

package pacman.test;

import pacman.game.info.GameInfo;
import java.util.EnumMap;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;
import pacman.game.Constants;
import pacman.game.comms.Messenger;
import pacman.game.Game;
import pacman.game.comms.BasicMessenger;

public class ForwardModelTest
{
    public static void main(final String[] args) {
        final Game game = new Game(System.currentTimeMillis(), new BasicMessenger(0, 1, 1));
        game.copy(new PacMan(game.getPacmanCurrentNodeIndex(), Constants.MOVE.DOWN, 2, true));
        final GameInfo info = game.getBlankGameInfo();
        info.setPacman(new PacMan(game.getPacmanCurrentNodeIndex(), Constants.MOVE.DOWN, game.getPacmanNumberOfLivesRemaining(), true));
        info.setGhostIndex(Constants.GHOST.INKY, new Ghost(Constants.GHOST.INKY, 10, 0, 0, Constants.MOVE.NEUTRAL));
        final Game next = game.getGameFromInfo(info);
        for (int i = 0; i < 100; ++i) {
            final EnumMap<Constants.GHOST, Constants.MOVE> inkyMove = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
            inkyMove.put(Constants.GHOST.INKY, Constants.MOVE.LEFT);
            next.advanceGame(Constants.MOVE.DOWN, inkyMove);
            System.out.println(next.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
        }
        System.out.println("Finished");
    }
}
