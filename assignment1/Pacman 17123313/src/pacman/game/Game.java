// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game;

import java.io.IOException;
import pacman.game.info.GameInfo;
import java.util.Map;
import java.util.Iterator;
import pacman.game.internal.Node;
import pacman.game.comms.Messenger;
import java.util.Random;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;
import java.util.EnumMap;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.game.internal.POType;
import pacman.game.internal.Maze;
import pacman.game.internal.PathsCache;

public final class Game
{
    public static PathsCache[] caches;
    private static Maze[] mazes;
    public POType PO_TYPE;
    public int SIGHT_LIMIT;
    protected boolean ghostsPresent;
    protected boolean pillsPresent;
    protected boolean powerPillsPresent;
    private BitSet pills;
    private BitSet powerPills;
    private int mazeIndex;
    private int levelCount;
    private int currentLevelTime;
    private int totalTime;
    private int score;
    private int ghostEatMultiplier;
    private int timeOfLastGlobalReversal;
    private boolean gameOver;
    private boolean pacmanWasEaten;
    private boolean pillWasEaten;
    private boolean powerPillWasEaten;
    private EnumMap<Constants.GHOST, Boolean> ghostsEaten;
    private PacMan pacman;
    private EnumMap<Constants.GHOST, Ghost> ghosts;
    private boolean po;
    private boolean beenBlanked;
    private int agent;
    private Maze currentMaze;
    private Random rnd;
    private long seed;
    private Messenger messenger;
    
    public Game(final long seed) {
        this(seed, null);
    }
    
    public Game(final long seed, final int initialMaze) {
        this(seed, initialMaze, null);
    }
    
    public Game(final long seed, final Messenger messenger) {
        this(seed, 0, messenger);
    }
    
    public Game(final long seed, final int initialMaze, final Messenger messenger) {
        this.PO_TYPE = POType.LOS;
        this.SIGHT_LIMIT = 100;
        this.ghostsPresent = true;
        this.pillsPresent = true;
        this.powerPillsPresent = true;
        this.agent = 0;
        this.seed = seed;
        this.rnd = new Random(seed);
        this.messenger = messenger;
        this._init(initialMaze);
    }
    
    private Game() {
        this.PO_TYPE = POType.LOS;
        this.SIGHT_LIMIT = 100;
        this.ghostsPresent = true;
        this.pillsPresent = true;
        this.powerPillsPresent = true;
        this.agent = 0;
    }
    
    private int getNodeIndexOfOwner() {
        if (this.agent >= 4) {
            return this.pacman.currentNodeIndex;
        }
        return this.ghosts.get(Constants.GHOST.values()[this.agent]).currentNodeIndex;
    }
    
    public boolean isNodeObservable(final int nodeIndex) {
        if (!this.po) {
            return true;
        }
        if (nodeIndex == -1) {
            return false;
        }
        final Node currentNode = Game.mazes[this.mazeIndex].graph[this.getNodeIndexOfOwner()];
        final Node check = Game.mazes[this.mazeIndex].graph[nodeIndex];
        Label_0364: {
            switch (this.PO_TYPE) {
                case LOS: {
                    return (currentNode.x == check.x || currentNode.y == check.y) && this.straightRouteBlocked(currentNode, check);
                }
                case RADIUS: {
                    final double manhattan = this.getManhattanDistance(currentNode.nodeIndex, check.nodeIndex);
                    return manhattan <= this.SIGHT_LIMIT;
                }
                case FF_LOS: {
                    if (currentNode.x != check.x && currentNode.y != check.y) {
                        break;
                    }
                    final Constants.MOVE previousMove = (this.agent >= 4) ? this.pacman.lastMoveMade : this.ghosts.get(Constants.GHOST.values()[this.agent]).lastMoveMade;
                    switch (previousMove) {
                        case UP: {
                            if (currentNode.x == check.x && currentNode.y >= check.y) {
                                return this.straightRouteBlocked(currentNode, check);
                            }
                            break Label_0364;
                        }
                        case DOWN: {
                            if (currentNode.x == check.x && currentNode.y <= check.y) {
                                return this.straightRouteBlocked(currentNode, check);
                            }
                            break Label_0364;
                        }
                        case LEFT: {
                            if (currentNode.y == check.y && currentNode.x >= check.x) {
                                return this.straightRouteBlocked(currentNode, check);
                            }
                            break Label_0364;
                        }
                        case RIGHT: {
                            if (currentNode.y == check.y && currentNode.x <= check.x) {
                                return this.straightRouteBlocked(currentNode, check);
                            }
                            break Label_0364;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    private boolean straightRouteBlocked(final Node startNode, final Node endNode) {
        final double manhattan = this.getManhattanDistance(startNode.nodeIndex, endNode.nodeIndex);
        if (manhattan <= this.SIGHT_LIMIT) {
            final double shortestPath = this.getShortestPathDistance(startNode.nodeIndex, endNode.nodeIndex);
            return manhattan == shortestPath;
        }
        return false;
    }
    
    private void _init(final int initialMaze) {
        this.mazeIndex = initialMaze;
        final int n = 0;
        this.totalTime = n;
        this.levelCount = n;
        this.currentLevelTime = n;
        this.score = n;
        this.ghostEatMultiplier = 1;
        this.gameOver = false;
        this.timeOfLastGlobalReversal = -1;
        this.pacmanWasEaten = false;
        this.pillWasEaten = false;
        this.powerPillWasEaten = false;
        this.ghostsEaten = new EnumMap<Constants.GHOST, Boolean>(Constants.GHOST.class);
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            this.ghostsEaten.put(ghost, false);
        }
        this._setPills(this.currentMaze = Game.mazes[this.mazeIndex]);
        this._initGhosts();
        this.pacman = new PacMan(this.currentMaze.initialPacManNodeIndex, Constants.MOVE.LEFT, 3, false);
    }
    
    private void _newLevelReset() {
        this.mazeIndex = ++this.mazeIndex % 4;
        ++this.levelCount;
        this.currentMaze = Game.mazes[this.mazeIndex];
        this.currentLevelTime = 0;
        this.ghostEatMultiplier = 1;
        this._setPills(this.currentMaze);
        this._levelReset();
    }
    
    private void _levelReset() {
        this.ghostEatMultiplier = 1;
        this._initGhosts();
        this.pacman.currentNodeIndex = this.currentMaze.initialPacManNodeIndex;
        this.pacman.lastMoveMade = Constants.MOVE.LEFT;
    }
    
    private void _setPills(final Maze maze) {
        if (this.pillsPresent) {
            (this.pills = new BitSet(this.currentMaze.pillIndices.length)).set(0, this.currentMaze.pillIndices.length);
        }
        if (this.powerPillsPresent) {
            (this.powerPills = new BitSet(this.currentMaze.powerPillIndices.length)).set(0, this.currentMaze.powerPillIndices.length);
        }
    }
    
    private void _initGhosts() {
        this.ghosts = new EnumMap<Constants.GHOST, Ghost>(Constants.GHOST.class);
        for (final Constants.GHOST ghostType : Constants.GHOST.values()) {
            this.ghosts.put(ghostType, new Ghost(ghostType, this.currentMaze.lairNodeIndex, 0, (int)(ghostType.initialLairTime * Math.pow(0.8999999761581421, this.levelCount % 6)), Constants.MOVE.NEUTRAL));
        }
    }
    
    public String getGameState() {
        if (this.po) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mazeIndex + "," + this.totalTime + "," + this.score + "," + this.currentLevelTime + "," + this.levelCount + "," + this.pacman.currentNodeIndex + "," + this.pacman.lastMoveMade + "," + this.pacman.numberOfLivesRemaining + "," + this.pacman.hasReceivedExtraLife + ",");
        for (final Ghost ghost : this.ghosts.values()) {
            sb.append(ghost.currentNodeIndex + "," + ghost.edibleTime + "," + ghost.lairTime + "," + ghost.lastMoveMade + ",");
        }
        for (int i = 0; i < this.currentMaze.pillIndices.length; ++i) {
            if (this.pills.get(i)) {
                sb.append("1");
            }
            else {
                sb.append("0");
            }
        }
        sb.append(",");
        for (int i = 0; i < this.currentMaze.powerPillIndices.length; ++i) {
            if (this.powerPills.get(i)) {
                sb.append("1");
            }
            else {
                sb.append("0");
            }
        }
        sb.append(",");
        sb.append(this.timeOfLastGlobalReversal);
        sb.append(",");
        sb.append(this.pacmanWasEaten);
        sb.append(",");
        for (final Constants.GHOST ghost2 : Constants.GHOST.values()) {
            sb.append(this.ghostsEaten.get(ghost2));
            sb.append(",");
        }
        sb.append(this.pillWasEaten);
        sb.append(",");
        sb.append(this.powerPillWasEaten);
        return sb.toString();
    }
    
    public void setGameState(final String gameState) {
        final String[] values = gameState.split(",");
        int index = 0;
        this.mazeIndex = Integer.parseInt(values[index++]);
        this.totalTime = Integer.parseInt(values[index++]);
        this.score = Integer.parseInt(values[index++]);
        this.currentLevelTime = Integer.parseInt(values[index++]);
        this.levelCount = Integer.parseInt(values[index++]);
        this.pacman = new PacMan(Integer.parseInt(values[index++]), Constants.MOVE.valueOf(values[index++]), Integer.parseInt(values[index++]), Boolean.parseBoolean(values[index++]));
        this.ghosts = new EnumMap<Constants.GHOST, Ghost>(Constants.GHOST.class);
        for (final Constants.GHOST ghostType : Constants.GHOST.values()) {
            this.ghosts.put(ghostType, new Ghost(ghostType, Integer.parseInt(values[index++]), Integer.parseInt(values[index++]), Integer.parseInt(values[index++]), Constants.MOVE.valueOf(values[index++])));
        }
        this._setPills(this.currentMaze = Game.mazes[this.mazeIndex]);
        for (int i = 0; i < values[index].length(); ++i) {
            if (values[index].charAt(i) == '1') {
                this.pills.set(i);
            }
            else {
                this.pills.clear(i);
            }
        }
        ++index;
        for (int i = 0; i < values[index].length(); ++i) {
            if (values[index].charAt(i) == '1') {
                this.powerPills.set(i);
            }
            else {
                this.powerPills.clear(i);
            }
        }
        this.timeOfLastGlobalReversal = Integer.parseInt(values[++index]);
        this.pacmanWasEaten = Boolean.parseBoolean(values[++index]);
        this.ghostsEaten = new EnumMap<Constants.GHOST, Boolean>(Constants.GHOST.class);
        for (final Constants.GHOST ghost : Constants.GHOST.values()) {
            this.ghostsEaten.put(ghost, Boolean.parseBoolean(values[++index]));
        }
        this.pillWasEaten = Boolean.parseBoolean(values[++index]);
        this.powerPillWasEaten = Boolean.parseBoolean(values[++index]);
    }
    
    public Game copy(final boolean copyMessenger) {
        final Game copy = new Game();
        copy.seed = this.seed;
        copy.rnd = new Random(this.seed);
        copy.currentMaze = this.currentMaze;
        copy.pills = (BitSet)this.pills.clone();
        copy.powerPills = (BitSet)this.powerPills.clone();
        copy.mazeIndex = this.mazeIndex;
        copy.levelCount = this.levelCount;
        copy.currentLevelTime = this.currentLevelTime;
        copy.totalTime = this.totalTime;
        copy.score = this.score;
        copy.ghostEatMultiplier = this.ghostEatMultiplier;
        copy.gameOver = this.gameOver;
        copy.timeOfLastGlobalReversal = this.timeOfLastGlobalReversal;
        copy.pacmanWasEaten = this.pacmanWasEaten;
        copy.pillWasEaten = this.pillWasEaten;
        copy.powerPillWasEaten = this.powerPillWasEaten;
        copy.pacman = this.pacman.copy();
        copy.SIGHT_LIMIT = this.SIGHT_LIMIT;
        copy.PO_TYPE = this.PO_TYPE;
        copy.ghostsPresent = this.ghostsPresent;
        copy.pillsPresent = this.pillsPresent;
        copy.powerPillsPresent = this.powerPillsPresent;
        copy.ghostsEaten = new EnumMap<Constants.GHOST, Boolean>(Constants.GHOST.class);
        copy.ghosts = new EnumMap<Constants.GHOST, Ghost>(Constants.GHOST.class);
        for (final Constants.GHOST ghostType : Constants.GHOST.values()) {
            copy.ghosts.put(ghostType, this.ghosts.get(ghostType).copy());
            copy.ghostsEaten.put(ghostType, this.ghostsEaten.get(ghostType));
        }
        copy.po = this.po;
        copy.agent = this.agent;
        if (this.hasMessaging()) {
            copy.messenger = (copyMessenger ? this.messenger.copy() : this.messenger);
        }
        return copy;
    }
    
    public Game copy() {
        return this.copy(false);
    }
    
    public Game copy(final Constants.GHOST ghost) {
        return this.copy(ghost, false);
    }
    
    public Game copy(final Constants.GHOST ghost, final boolean copyMessenger) {
        final Game game = this.copy();
        game.po = true;
        game.agent = ghost.ordinal();
        return game;
    }
    
    public Game copy(final PacMan pacman) {
        final Game game = this.copy();
        game.po = true;
        game.agent = Constants.GHOST.values().length + 1;
        return game;
    }
    
    public Game copy(final int agent) {
        final Game game = this.copy();
        if (agent == -1) {
            return game;
        }
        game.po = true;
        game.agent = agent;
        return game;
    }
    
    private boolean canBeForwarded() {
        return !this.po || this.beenBlanked;
    }
    
    public void advanceGame(final Constants.MOVE pacManMove, final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        this.updatePacMan(pacManMove);
        this.updateGhosts(ghostMoves);
        this.updateGame();
    }
    
    public void advanceGameWithoutReverse(final Constants.MOVE pacManMove, final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        this.updatePacMan(pacManMove);
        this.updateGhostsWithoutReverse(ghostMoves);
        this.updateGame();
    }
    
    public void advanceGameWithForcedReverse(final Constants.MOVE pacManMove, final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        this.updatePacMan(pacManMove);
        this.updateGhostsWithForcedReverse(ghostMoves);
        this.updateGame();
    }
    
    public void advanceGameWithPowerPillReverseOnly(final Constants.MOVE pacManMove, final EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        this.updatePacMan(pacManMove);
        if (this.powerPillWasEaten) {
            this.updateGhostsWithForcedReverse(ghostMoves);
        }
        else {
            this.updateGhostsWithoutReverse(ghostMoves);
        }
        this.updateGame();
    }
    
    public void updatePacMan(final Constants.MOVE pacManMove) {
        if (!this.canBeForwarded()) {
            return;
        }
        this._updatePacMan(pacManMove);
        this._eatPill();
        this._eatPowerPill();
    }
    
    public void updateGhosts(EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        if (!this.ghostsPresent) {
            return;
        }
        ghostMoves = this._completeGhostMoves(ghostMoves);
        if (!this._reverseGhosts(ghostMoves, false)) {
            this._updateGhosts(ghostMoves);
        }
    }
    
    public void updateGhostsWithoutReverse(EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        if (!this.ghostsPresent) {
            return;
        }
        ghostMoves = this._completeGhostMoves(ghostMoves);
        this._updateGhosts(ghostMoves);
    }
    
    public void updateGhostsWithForcedReverse(EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves) {
        if (!this.canBeForwarded()) {
            return;
        }
        if (!this.ghostsPresent) {
            return;
        }
        ghostMoves = this._completeGhostMoves(ghostMoves);
        this._reverseGhosts(ghostMoves, true);
    }
    
    public void updateGame() {
        if (!this.canBeForwarded()) {
            return;
        }
        this._feast();
        this._updateLairTimes();
        this._updatePacManExtraLife();
        ++this.totalTime;
        ++this.currentLevelTime;
        this._checkLevelState();
        if (this.messenger != null) {
            this.messenger.update();
        }
    }
    
    public void updateGame(final boolean feast, final boolean updateLairTimes, final boolean updateExtraLife, final boolean updateTotalTime, final boolean updateLevelTime) {
        if (!this.canBeForwarded()) {
            return;
        }
        if (feast) {
            this._feast();
        }
        if (updateLairTimes) {
            this._updateLairTimes();
        }
        if (updateExtraLife) {
            this._updatePacManExtraLife();
        }
        if (updateTotalTime) {
            ++this.totalTime;
        }
        if (updateLevelTime) {
            ++this.currentLevelTime;
        }
        this._checkLevelState();
        if (this.messenger != null) {
            this.messenger.update();
        }
    }
    
    private void _updateLairTimes() {
        if (!this.ghostsPresent) {
            return;
        }
        for (final Ghost ghost : this.ghosts.values()) {
            if (ghost.lairTime > 0) {
                final Ghost ghost2 = ghost;
                if (--ghost2.lairTime != 0) {
                    continue;
                }
                ghost.currentNodeIndex = this.currentMaze.initialGhostNodeIndex;
            }
        }
    }
    
    private void _updatePacManExtraLife() {
        if (!this.pacman.hasReceivedExtraLife && this.score >= 10000) {
            this.pacman.hasReceivedExtraLife = true;
            final PacMan pacman = this.pacman;
            ++pacman.numberOfLivesRemaining;
        }
    }
    
    private void _updatePacMan(final Constants.MOVE move) {
        this.pacman.lastMoveMade = this._correctPacManDir(move);
        this.pacman.currentNodeIndex = ((this.pacman.lastMoveMade == Constants.MOVE.NEUTRAL) ? this.pacman.currentNodeIndex : this.currentMaze.graph[this.pacman.currentNodeIndex].neighbourhood.get(this.pacman.lastMoveMade));
    }
    
    private Constants.MOVE _correctPacManDir(final Constants.MOVE direction) {
        final Node node = this.currentMaze.graph[this.pacman.currentNodeIndex];
        if (node.neighbourhood.containsKey(direction)) {
            return direction;
        }
        if (node.neighbourhood.containsKey(this.pacman.lastMoveMade)) {
            return this.pacman.lastMoveMade;
        }
        return Constants.MOVE.NEUTRAL;
    }
    
    private void _updateGhosts(final EnumMap<Constants.GHOST, Constants.MOVE> moves) {
        for (final Map.Entry<Constants.GHOST, Constants.MOVE> entry : moves.entrySet()) {
            final Ghost ghost = this.ghosts.get(entry.getKey());
            if (ghost.lairTime == 0 && (ghost.edibleTime == 0 || ghost.edibleTime % 2 != 0)) {
                ghost.lastMoveMade = this._checkGhostDir(ghost, entry.getValue());
                moves.put(entry.getKey(), ghost.lastMoveMade);
                ghost.currentNodeIndex = this.currentMaze.graph[ghost.currentNodeIndex].neighbourhood.get(ghost.lastMoveMade);
            }
        }
    }
    
    private EnumMap<Constants.GHOST, Constants.MOVE> _completeGhostMoves(EnumMap<Constants.GHOST, Constants.MOVE> moves) {
        if (moves == null) {
            moves = new EnumMap<Constants.GHOST, Constants.MOVE>(Constants.GHOST.class);
            for (final Map.Entry<Constants.GHOST, Ghost> entry : this.ghosts.entrySet()) {
                moves.put(entry.getKey(), entry.getValue().lastMoveMade);
            }
        }
        if (moves.size() < 4) {
            for (final Constants.GHOST ghostType : this.ghosts.keySet()) {
                if (!moves.containsKey(ghostType)) {
                    moves.put(ghostType, Constants.MOVE.NEUTRAL);
                }
            }
        }
        return moves;
    }
    
    private Constants.MOVE _checkGhostDir(final Ghost ghost, final Constants.MOVE direction) {
        final Node node = this.currentMaze.graph[ghost.currentNodeIndex];
        if (node.neighbourhood.containsKey(direction) && direction != ghost.lastMoveMade.opposite()) {
            return direction;
        }
        if (node.neighbourhood.containsKey(ghost.lastMoveMade)) {
            return ghost.lastMoveMade;
        }
        final Constants.MOVE[] moves = node.allPossibleMoves.get(ghost.lastMoveMade);
        return moves[this.rnd.nextInt(moves.length)];
    }
    
    private void _eatPill() {
        this.pillWasEaten = false;
        final int pillIndex = this.currentMaze.graph[this.pacman.currentNodeIndex].pillIndex;
        if (pillIndex >= 0 && this.pills.get(pillIndex)) {
            this.score += 10;
            this.pills.clear(pillIndex);
            this.pillWasEaten = true;
        }
    }
    
    private void _eatPowerPill() {
        this.powerPillWasEaten = false;
        final int powerPillIndex = this.currentMaze.graph[this.pacman.currentNodeIndex].powerPillIndex;
        if (powerPillIndex >= 0 && this.powerPills.get(powerPillIndex)) {
            this.score += 50;
            this.ghostEatMultiplier = 1;
            this.powerPills.clear(powerPillIndex);
            final int newEdibleTime = (int)(200.0 * Math.pow(0.8999999761581421, this.levelCount % 6));
            for (final Ghost ghost : this.ghosts.values()) {
                if (ghost.lairTime == 0) {
                    ghost.edibleTime = newEdibleTime;
                }
                else {
                    ghost.edibleTime = 0;
                }
            }
            this.powerPillWasEaten = true;
        }
    }
    
    private boolean _reverseGhosts(final EnumMap<Constants.GHOST, Constants.MOVE> moves, final boolean force) {
        boolean reversed = false;
        boolean globalReverse = false;
        if (Math.random() < 0.001500000013038516) {
            globalReverse = true;
        }
        for (final Map.Entry<Constants.GHOST, Constants.MOVE> entry : moves.entrySet()) {
            final Ghost ghost = this.ghosts.get(entry.getKey());
            if (this.currentLevelTime > 1 && ghost.lairTime == 0 && ghost.lastMoveMade != Constants.MOVE.NEUTRAL && (force || this.powerPillWasEaten || globalReverse)) {
                ghost.lastMoveMade = ghost.lastMoveMade.opposite();
                ghost.currentNodeIndex = this.currentMaze.graph[ghost.currentNodeIndex].neighbourhood.get(ghost.lastMoveMade);
                reversed = true;
                this.timeOfLastGlobalReversal = this.totalTime;
            }
        }
        return reversed;
    }
    
    private void _feast() {
        this.pacmanWasEaten = false;
        for (final Constants.GHOST ghost : this.ghosts.keySet()) {
            this.ghostsEaten.put(ghost, false);
        }
        for (final Ghost ghost2 : this.ghosts.values()) {
            final int distance = this.getShortestPathDistance(this.pacman.currentNodeIndex, ghost2.currentNodeIndex);
            if (distance <= 2 && distance != -1) {
                if (ghost2.edibleTime <= 0) {
                    final PacMan pacman = this.pacman;
                    --pacman.numberOfLivesRemaining;
                    this.pacmanWasEaten = true;
                    if (this.pacman.numberOfLivesRemaining <= 0) {
                        this.gameOver = true;
                    }
                    else {
                        this._levelReset();
                    }
                    return;
                }
                this.score += 200 * this.ghostEatMultiplier;
                this.ghostEatMultiplier *= 2;
                ghost2.edibleTime = 0;
                ghost2.lairTime = (int)(40.0 * Math.pow(0.8999999761581421, this.levelCount % 6));
                ghost2.currentNodeIndex = this.currentMaze.lairNodeIndex;
                ghost2.lastMoveMade = Constants.MOVE.NEUTRAL;
                this.ghostsEaten.put(ghost2.type, true);
            }
        }
        for (final Ghost ghost2 : this.ghosts.values()) {
            if (ghost2.edibleTime > 0) {
                final Ghost ghost3 = ghost2;
                --ghost3.edibleTime;
            }
        }
    }
    
    private void _checkLevelState() {
        if (this.totalTime + 1 > 24000) {
            this.gameOver = true;
            this.score += this.pacman.numberOfLivesRemaining * 800;
        }
        else if ((this.pills.isEmpty() && this.powerPills.isEmpty()) || this.currentLevelTime >= 4000) {
            this._newLevelReset();
        }
    }
    
    public boolean wasPacManEaten() {
        return this.pacmanWasEaten;
    }
    
    public boolean wasGhostEaten(final Constants.GHOST ghost) {
        return this.ghostsEaten.get(ghost);
    }
    
    public int getNumGhostsEaten() {
        int count = 0;
        for (final Constants.GHOST ghost : this.ghosts.keySet()) {
            if (this.ghostsEaten.get(ghost)) {
                ++count;
            }
        }
        return count;
    }
    
    public boolean wasPillEaten() {
        return this.pillWasEaten;
    }
    
    public boolean wasPowerPillEaten() {
        return this.powerPillWasEaten;
    }
    
    public int getTimeOfLastGlobalReversal() {
        return this.timeOfLastGlobalReversal;
    }
    
    public boolean gameOver() {
        return this.gameOver;
    }
    
    public Maze getCurrentMaze() {
        return this.currentMaze;
    }
    
    public int getNodeXCood(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].x;
    }
    
    public int getNodeYCood(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].y;
    }
    
    public int getMazeIndex() {
        return this.mazeIndex;
    }
    
    public int getCurrentLevel() {
        return this.levelCount;
    }
    
    public int getNumberOfNodes() {
        return this.currentMaze.graph.length;
    }
    
    public int getGhostCurrentEdibleScore() {
        return 200 * this.ghostEatMultiplier;
    }
    
    public int getGhostInitialNodeIndex() {
        return this.currentMaze.initialGhostNodeIndex;
    }
    
    public Boolean isPillStillAvailable(final int pillIndex) {
        if (this.po) {
            final int pillLocation = this.currentMaze.pillIndices[pillIndex];
            if (!this.isNodeObservable(pillLocation)) {
                return null;
            }
        }
        return this.pills.get(pillIndex);
    }
    
    public Boolean isPowerPillStillAvailable(final int powerPillIndex) {
        if (this.po) {
            final int pillLocation = this.currentMaze.powerPillIndices[powerPillIndex];
            if (!this.isNodeObservable(pillLocation)) {
                return null;
            }
        }
        return this.powerPills.get(powerPillIndex);
    }
    
    public int getPillIndex(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].pillIndex;
    }
    
    public int getPowerPillIndex(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].powerPillIndex;
    }
    
    public int[] getJunctionIndices() {
        return this.currentMaze.junctionIndices;
    }
    
    public int[] getPillIndices() {
        if (this.po) {
            final int[] indices = this.currentMaze.pillIndices;
            final int[] results = new int[indices.length];
            int i = 0;
            for (final int index : indices) {
                if (this.isNodeObservable(index)) {
                    results[i] = index;
                    ++i;
                }
            }
            final int[] temp = new int[i];
            System.arraycopy(results, 0, temp, 0, i);
            return temp;
        }
        return this.currentMaze.pillIndices;
    }
    
    public int[] getPowerPillIndices() {
        if (this.po) {
            final int[] indices = this.currentMaze.powerPillIndices;
            final int[] results = new int[indices.length];
            int i = 0;
            for (final int index : indices) {
                if (this.isNodeObservable(index)) {
                    results[i] = index;
                    ++i;
                }
            }
            final int[] temp = new int[i];
            System.arraycopy(results, 0, temp, 0, i);
            return temp;
        }
        return this.currentMaze.powerPillIndices;
    }
    
    public int getPacmanCurrentNodeIndex() {
        if (this.po && !this.isNodeObservable(this.pacman.currentNodeIndex)) {
            return -1;
        }
        return this.pacman.currentNodeIndex;
    }
    
    public Constants.MOVE getPacmanLastMoveMade() {
        if (this.po && !this.isNodeObservable(this.pacman.currentNodeIndex)) {
            return null;
        }
        return this.pacman.lastMoveMade;
    }
    
    public int getPacmanNumberOfLivesRemaining() {
        return this.pacman.numberOfLivesRemaining;
    }
    
    public int getGhostCurrentNodeIndex(final Constants.GHOST ghostType) {
        if (this.po) {
            final int index = this.ghosts.get(ghostType).currentNodeIndex;
            return this.isNodeObservable(index) ? index : -1;
        }
        return this.ghosts.get(ghostType).currentNodeIndex;
    }
    
    public Constants.MOVE getGhostLastMoveMade(final Constants.GHOST ghostType) {
        if (this.po) {
            final Ghost ghost = this.ghosts.get(ghostType);
            return this.isNodeObservable(ghost.currentNodeIndex) ? ghost.lastMoveMade : null;
        }
        return this.ghosts.get(ghostType).lastMoveMade;
    }
    
    public int getGhostEdibleTime(final Constants.GHOST ghostType) {
        if (this.po) {
            final Ghost ghost = this.ghosts.get(ghostType);
            return this.isNodeObservable(ghost.currentNodeIndex) ? ghost.edibleTime : -1;
        }
        return this.ghosts.get(ghostType).edibleTime;
    }
    
    public Boolean isGhostEdible(final Constants.GHOST ghostType) {
        if (this.po) {
            final Ghost ghost = this.ghosts.get(ghostType);
            return this.isNodeObservable(ghost.currentNodeIndex) ? Boolean.valueOf(ghost.edibleTime > 0) : null;
        }
        return this.ghosts.get(ghostType).edibleTime > 0;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public int getCurrentLevelTime() {
        return this.currentLevelTime;
    }
    
    public int getTotalTime() {
        return this.totalTime;
    }
    
    public int getNumberOfPills() {
        return this.currentMaze.pillIndices.length;
    }
    
    public int getNumberOfPowerPills() {
        return this.currentMaze.powerPillIndices.length;
    }
    
    public int getNumberOfActivePills() {
        return this.pills.cardinality();
    }
    
    public int getNumberOfActivePowerPills() {
        return this.powerPills.cardinality();
    }
    
    public int getGhostLairTime(final Constants.GHOST ghostType) {
        if (this.po) {
            final Ghost ghost = this.ghosts.get(ghostType);
            return this.isNodeObservable(ghost.currentNodeIndex) ? ghost.lairTime : -1;
        }
        return this.ghosts.get(ghostType).lairTime;
    }
    
    public int[] getActivePillsIndices() {
        final int[] indices = new int[this.pills.cardinality()];
        int index = 0;
        for (int i = 0; i < this.currentMaze.pillIndices.length; ++i) {
            if ((!this.po || this.isNodeObservable(this.currentMaze.pillIndices[i])) && this.pills.get(i)) {
                indices[index++] = this.currentMaze.pillIndices[i];
            }
        }
        if (index != indices.length) {
            final int[] results = new int[index];
            System.arraycopy(indices, 0, results, 0, index);
            return results;
        }
        return indices;
    }
    
    public int[] getActivePowerPillsIndices() {
        final int[] indices = new int[this.powerPills.cardinality()];
        int index = 0;
        for (int i = 0; i < this.currentMaze.powerPillIndices.length; ++i) {
            if ((!this.po || this.isNodeObservable(this.currentMaze.powerPillIndices[i])) && this.powerPills.get(i)) {
                indices[index++] = this.currentMaze.powerPillIndices[i];
            }
        }
        if (index != indices.length) {
            final int[] results = new int[index];
            System.arraycopy(indices, 0, results, 0, index);
            return results;
        }
        return indices;
    }
    
    public Boolean doesGhostRequireAction(final Constants.GHOST ghostType) {
        if (!this.po || this.isNodeObservable(this.ghosts.get(ghostType).currentNodeIndex)) {
            return (this.isJunction(this.ghosts.get(ghostType).currentNodeIndex) || (this.ghosts.get(ghostType).lastMoveMade == Constants.MOVE.NEUTRAL && this.ghosts.get(ghostType).currentNodeIndex == this.currentMaze.initialGhostNodeIndex)) && (this.ghosts.get(ghostType).edibleTime == 0 || this.ghosts.get(ghostType).edibleTime % 2 != 0);
        }
        return null;
    }
    
    public boolean isJunction(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].numNeighbouringNodes > 2;
    }
    
    public Constants.MOVE[] getPossibleMoves(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].allPossibleMoves.get(Constants.MOVE.NEUTRAL);
    }
    
    public Constants.MOVE[] getPossibleMoves(final int nodeIndex, final Constants.MOVE lastModeMade) {
        return this.currentMaze.graph[nodeIndex].allPossibleMoves.get(lastModeMade);
    }
    
    public int[] getNeighbouringNodes(final int nodeIndex) {
        return this.currentMaze.graph[nodeIndex].allNeighbouringNodes.get(Constants.MOVE.NEUTRAL);
    }
    
    public int[] getNeighbouringNodes(final int nodeIndex, final Constants.MOVE lastModeMade) {
        return this.currentMaze.graph[nodeIndex].allNeighbouringNodes.get(lastModeMade);
    }
    
    public int getNeighbour(final int nodeIndex, final Constants.MOVE moveToBeMade) {
        final Integer neighbour = this.currentMaze.graph[nodeIndex].neighbourhood.get(moveToBeMade);
        return (neighbour == null) ? -1 : neighbour;
    }
    
    public Constants.MOVE getMoveToMakeToReachDirectNeighbour(final int currentNodeIndex, final int neighbourNodeIndex) {
        for (final Constants.MOVE move : Constants.MOVE.values()) {
            if (this.currentMaze.graph[currentNodeIndex].neighbourhood.containsKey(move) && this.currentMaze.graph[currentNodeIndex].neighbourhood.get(move) == neighbourNodeIndex) {
                return move;
            }
        }
        return null;
    }
    
    public int getShortestPathDistance(final int fromNodeIndex, final int toNodeIndex) {
        if (fromNodeIndex == toNodeIndex) {
            return 0;
        }
        if (fromNodeIndex < toNodeIndex) {
            return this.currentMaze.shortestPathDistances[toNodeIndex * (toNodeIndex + 1) / 2 + fromNodeIndex];
        }
        return this.currentMaze.shortestPathDistances[fromNodeIndex * (fromNodeIndex + 1) / 2 + toNodeIndex];
    }
    
    public double getEuclideanDistance(final int fromNodeIndex, final int toNodeIndex) {
        return Math.sqrt(Math.pow(this.currentMaze.graph[fromNodeIndex].x - this.currentMaze.graph[toNodeIndex].x, 2.0) + Math.pow(this.currentMaze.graph[fromNodeIndex].y - this.currentMaze.graph[toNodeIndex].y, 2.0));
    }
    
    public int getManhattanDistance(final int fromNodeIndex, final int toNodeIndex) {
        return Math.abs(this.currentMaze.graph[fromNodeIndex].x - this.currentMaze.graph[toNodeIndex].x) + Math.abs(this.currentMaze.graph[fromNodeIndex].y - this.currentMaze.graph[toNodeIndex].y);
    }
    
    public double getDistance(final int fromNodeIndex, final int toNodeIndex, final Constants.DM distanceMeasure) {
        switch (distanceMeasure) {
            case PATH: {
                return this.getShortestPathDistance(fromNodeIndex, toNodeIndex);
            }
            case EUCLID: {
                return this.getEuclideanDistance(fromNodeIndex, toNodeIndex);
            }
            case MANHATTAN: {
                return this.getManhattanDistance(fromNodeIndex, toNodeIndex);
            }
            default: {
                return -1.0;
            }
        }
    }
    
    public double getDistance(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade, final Constants.DM distanceMeasure) {
        switch (distanceMeasure) {
            case PATH: {
                return this.getApproximateShortestPathDistance(fromNodeIndex, toNodeIndex, lastMoveMade);
            }
            case EUCLID: {
                return this.getEuclideanDistance(fromNodeIndex, toNodeIndex);
            }
            case MANHATTAN: {
                return this.getManhattanDistance(fromNodeIndex, toNodeIndex);
            }
            default: {
                return -1.0;
            }
        }
    }
    
    public int getClosestNodeIndexFromNodeIndex(final int fromNodeIndex, final int[] targetNodeIndices, final Constants.DM distanceMeasure) {
        double minDistance = 2.147483647E9;
        int target = -1;
        for (int i = 0; i < targetNodeIndices.length; ++i) {
            double distance = 0.0;
            distance = this.getDistance(targetNodeIndices[i], fromNodeIndex, distanceMeasure);
            if (distance < minDistance) {
                minDistance = distance;
                target = targetNodeIndices[i];
            }
        }
        return target;
    }
    
    public int getFarthestNodeIndexFromNodeIndex(final int fromNodeIndex, final int[] targetNodeIndices, final Constants.DM distanceMeasure) {
        double maxDistance = -2.147483648E9;
        int target = -1;
        for (int i = 0; i < targetNodeIndices.length; ++i) {
            double distance = 0.0;
            distance = this.getDistance(targetNodeIndices[i], fromNodeIndex, distanceMeasure);
            if (distance > maxDistance) {
                maxDistance = distance;
                target = targetNodeIndices[i];
            }
        }
        return target;
    }
    
    public Constants.MOVE getNextMoveTowardsTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double minDistance = 2.147483647E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].neighbourhood.entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, distanceMeasure);
            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    public Constants.MOVE getNextMoveAwayFromTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double maxDistance = -2.147483648E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].neighbourhood.entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, distanceMeasure);
            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    public Constants.MOVE getApproximateNextMoveTowardsTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double minDistance = 2.147483647E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, distanceMeasure);
            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    public Constants.MOVE getApproximateNextMoveAwayFromTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double maxDistance = -2.147483648E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, distanceMeasure);
            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    public Constants.MOVE getNextMoveTowardsTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double minDistance = 2.147483647E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, lastMoveMade, distanceMeasure);
            if (distance < minDistance) {
                minDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    public Constants.MOVE getNextMoveAwayFromTarget(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade, final Constants.DM distanceMeasure) {
        Constants.MOVE move = null;
        double maxDistance = -2.147483648E9;
        for (final Map.Entry<Constants.MOVE, Integer> entry : this.currentMaze.graph[fromNodeIndex].allNeighbourhoods.get(lastMoveMade).entrySet()) {
            final double distance = this.getDistance(entry.getValue(), toNodeIndex, lastMoveMade, distanceMeasure);
            if (distance > maxDistance) {
                maxDistance = distance;
                move = entry.getKey();
            }
        }
        return move;
    }
    
    @Deprecated
    public int[] getAStarPath(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade) {
        return this.getShortestPath(fromNodeIndex, toNodeIndex, lastMoveMade);
    }
    
    public int[] getShortestPath(final int fromNodeIndex, final int toNodeIndex) {
        return Game.caches[this.mazeIndex].getPathFromA2B(fromNodeIndex, toNodeIndex);
    }
    
    @Deprecated
    public int[] getApproximateShortestPath(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade) {
        return this.getShortestPath(fromNodeIndex, toNodeIndex, lastMoveMade);
    }
    
    public int[] getShortestPath(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade) {
        if (this.currentMaze.graph[fromNodeIndex].neighbourhood.size() == 0) {
            return new int[0];
        }
        return Game.caches[this.mazeIndex].getPathFromA2B(fromNodeIndex, toNodeIndex, lastMoveMade);
    }
    
    @Deprecated
    public int getApproximateShortestPathDistance(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade) {
        return this.getShortestPathDistance(fromNodeIndex, toNodeIndex, lastMoveMade);
    }
    
    public int getShortestPathDistance(final int fromNodeIndex, final int toNodeIndex, final Constants.MOVE lastMoveMade) {
        if (this.currentMaze.graph[fromNodeIndex].neighbourhood.size() == 0) {
            return 0;
        }
        return Game.caches[this.mazeIndex].getPathDistanceFromA2B(fromNodeIndex, toNodeIndex, lastMoveMade);
    }
    
    public boolean hasMessaging() {
        return this.messenger != null && this.agent < Constants.GHOST.values().length;
    }
    
    public Messenger getMessenger() {
        return this.hasMessaging() ? this.messenger : null;
    }
    
    public GameInfo getBlankGameInfo() {
        return new GameInfo(this.pills.length());
    }
    
    public Game getGameFromInfo(final GameInfo info) {
        final Game game = this.copy(false);
        game.messenger = null;
        game.pills = info.getPills();
        game.powerPills = info.getPowerPills();
        game.pacman = info.getPacman();
        game.ghosts = info.getGhosts();
        game.beenBlanked = true;
        game.po = false;
        return game;
    }
    
    public boolean isGamePo() {
        return this.po;
    }
    
    public void setGhostsPresent(final boolean ghostsPresent) {
        this.ghostsPresent = ghostsPresent;
    }
    
    public void setPillsPresent(final boolean pillsPresent) {
        this.pillsPresent = pillsPresent;
    }
    
    public void setPowerPillsPresent(final boolean powerPillsPresent) {
        this.powerPillsPresent = powerPillsPresent;
    }
    
    static {
        Game.caches = new PathsCache[4];
        Game.mazes = new Maze[4];
        for (int i = 0; i < Game.mazes.length; ++i) {
            try {
                Game.mazes[i] = new Maze(i);
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < Game.mazes.length; ++i) {
            Game.caches[i] = new PathsCache(i);
        }
    }
}
