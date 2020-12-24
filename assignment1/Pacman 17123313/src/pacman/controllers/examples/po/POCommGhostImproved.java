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

class POCommGhostImproved
{
    private static final float CONSISTENCY = 0.9f;
    private static final int PILL_PROXIMITY = 15;
    Random rnd;
    private int TICK_THRESHOLD;
    private Constants.GHOST ghost;
    private int lastPacmanIndex;
    private int tickSeen;
    
    public POCommGhostImproved(final Constants.GHOST ghost) {
        this(ghost, 5);
    }
    
    public POCommGhostImproved(final Constants.GHOST ghost, final int TICK_THRESHOLD) {
        this.rnd = new Random();
        this.lastPacmanIndex = -1;
        this.tickSeen = -1;
        this.ghost = ghost;
        this.TICK_THRESHOLD = TICK_THRESHOLD;
    }
    
    public Constants.MOVE getMove(final Game game, final long timeDue) {
        final int currentTick = game.getCurrentLevelTime();
        if (currentTick <= 2 || currentTick - this.tickSeen >= this.TICK_THRESHOLD) {
            this.lastPacmanIndex = -1;
            this.tickSeen = -1;
        }
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        final int currentIndex = game.getGhostCurrentNodeIndex(this.ghost);
        final Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1) {
            this.lastPacmanIndex = pacmanIndex;
            this.tickSeen = game.getCurrentLevelTime();
            if (messenger != null) {
                messenger.addMessage(new BasicMessage(this.ghost, null, Message.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
            }
        }
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (final Message message : messenger.getMessages(this.ghost)) {
                if (message.getType() == Message.MessageType.PACMAN_SEEN && message.getTick() > this.tickSeen && message.getTick() < currentTick) {
                    this.lastPacmanIndex = message.getData();
                    this.tickSeen = message.getTick();
                }
            }
        }
        if (pacmanIndex == -1) {
            pacmanIndex = this.lastPacmanIndex;
        }
        final Boolean requiresAction = game.doesGhostRequireAction(this.ghost);
        if (requiresAction != null && requiresAction) {
            if (pacmanIndex != -1) {
                Label_0358: {
                    if (game.getGhostEdibleTime(this.ghost) <= 0) {
                        if (!this.closeToPower(game)) {
                            break Label_0358;
                        }
                    }
                    try {
                        return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(this.ghost), game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(e);
                        System.out.println(pacmanIndex + " : " + currentIndex);
                        return null;
                    }
                }
                if (this.rnd.nextFloat() >= 0.9f) {
                    return null;
                }
                try {
                    final Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(this.ghost), pacmanIndex, game.getGhostLastMoveMade(this.ghost), Constants.DM.PATH);
                    return move;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e);
                    System.out.println(pacmanIndex + " : " + currentIndex);
                    return null;
                }
            }
            final Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(this.ghost), game.getGhostLastMoveMade(this.ghost));
            return possibleMoves[this.rnd.nextInt(possibleMoves.length)];
        }
        return null;
    }
    
    private boolean closeToPower(final Game game) {
        final int[] powerPills = game.getPowerPillIndices();
        for (int i = 0; i < powerPills.length; ++i) {
            final Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
            if (pacmanNodeIndex == -1) {
                pacmanNodeIndex = this.lastPacmanIndex;
            }
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
