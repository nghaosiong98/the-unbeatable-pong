// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers.examples.po;

import java.util.Iterator;
import pacman.game.comms.Messenger;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.Game;
import pacman.game.Constants;
import java.util.Random;

class POCommGhost
{
    private static final float CONSISTENCY = 0.9f;
    private static final int PILL_PROXIMITY = 15;
    Random rnd;
    private Constants.GHOST ghost;
    
    public POCommGhost(final Constants.GHOST ghost) {
        this.rnd = new Random();
        this.ghost = ghost;
    }
    
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        final Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1 && messenger != null) {
            messenger.addMessage(new BasicMessage(this.ghost, null, Message.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
        }
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (final Message message : messenger.getMessages(this.ghost)) {
                if (message.getType() == Message.MessageType.PACMAN_SEEN) {
                    pacmanIndex = message.getData();
                }
            }
        }
        if (!game.doesGhostRequireAction(this.ghost)) {
            return null;
        }
        if (game.getGhostEdibleTime(this.ghost) > 0 || this.closeToPower(game)) {
            return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(this.ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
        }
        if (pacmanIndex != -1 && this.rnd.nextFloat() < 0.9f) {
            final Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(this.ghost), pacmanIndex, game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
            return move;
        }
        final Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(this.ghost), game.getGhostLastMoveMade(this.ghost));
        return possibleMoves[this.rnd.nextInt(possibleMoves.length)];
    }
    
    private boolean closeToPower(final Game game) {
        final int[] powerPills = game.getPowerPillIndices();
        for (int i = 0; i < powerPills.length; ++i) {
            final Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            final int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < 15) {
                return true;
            }
        }
        return false;
    }
}
