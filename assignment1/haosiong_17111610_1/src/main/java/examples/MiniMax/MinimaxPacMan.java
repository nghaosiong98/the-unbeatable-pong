package examples.MiniMax;

import java.util.ArrayList;
import java.util.HashSet;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;

public class MinimaxPacMan extends PacmanController {
    final int MAX_DEPTH = 120;
    final int INITIAL_REWARD = 0;

    @Override
    public Constants.MOVE getMove(Game game, long l) {
        ArrayList<MyGhost> ghosts = new ArrayList<>();
        ghosts.add(new MyGhost(game, Constants.GHOST.BLINKY));
        ghosts.add(new MyGhost(game, Constants.GHOST.SUE));
        ghosts.add(new MyGhost(game, Constants.GHOST.INKY));
        ghosts.add(new MyGhost(game, Constants.GHOST.PINKY));
        HashSet<Integer> visitedNodes = new HashSet<>();

        int pacmanCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
        visitedNodes.add(pacmanCurrentNodeIndex);

        HashSet<Integer> pills = new HashSet<>();
        for (int index: game.getActivePillsIndices()) {
            pills.add(index);
        }

        for (int index: game.getActivePowerPillsIndices()) {
            pills.add(index);
        }

        MyMove pacmanMove = getPacManMove(
                game,
                pacmanCurrentNodeIndex,
                MAX_DEPTH,
                INITIAL_REWARD,
                Constants.MOVE.LEFT,
                visitedNodes,
                ghosts,
                pills
        );

        return pacmanMove.move;
    }

    MyMove getPacManMove(
            Game game,
            int pacmanCurrentNodeIndex,
            int depth,
            double reward,
            Constants.MOVE selectedMove,
            HashSet<Integer> visited,
            ArrayList<MyGhost> ghosts,
            HashSet<Integer> pills
    ) {
        if (depth == 0 || reward < 0) {
            return new MyMove(reward + 0.01 / visited.size(), selectedMove);
        }
        Constants.MOVE[] moves = game.getPossibleMoves(pacmanCurrentNodeIndex);

        Constants.MOVE bestMove = Constants.MOVE.NEUTRAL;
        double highestReward = Integer.MIN_VALUE;
        for (Constants.MOVE move: moves) {
            int newPacmanNodeIndex = game.getNeighbour(pacmanCurrentNodeIndex, move);

            if (visited.contains(newPacmanNodeIndex) || move == Constants.MOVE.NEUTRAL) {
                continue;
            } else {
                visited.add(newPacmanNodeIndex);
            }
            int localReward = 0;

            boolean containPill = false;
            if (pills.contains(newPacmanNodeIndex)) {
                localReward ++;
                containPill = true;
                pills.remove(newPacmanNodeIndex);
            }

            ArrayList<MyGhost> copyGhosts = new ArrayList<>();
            outer:for (MyGhost ghost: ghosts) {
                if (ghost.index != -1) {
                    MyMove ghostBestMoveIndex = getGhostMove(game, ghost, newPacmanNodeIndex);
                    int index = game.getNeighbour(ghost.index, ghostBestMoveIndex.move);

                    copyGhosts.add(new MyGhost(index, ghost.id));
                    if (ghostBestMoveIndex.reward == Integer.MAX_VALUE) {
                        localReward += Integer.MIN_VALUE;
                        break outer;
                    }
                }
            }

            MyMove innerPacmanMove = getPacManMove(
                    game,
                    newPacmanNodeIndex,
                    depth - 1,
                    reward + localReward,
                    move,
                    visited,
                    copyGhosts,
                    pills
            );
            if (innerPacmanMove.reward > highestReward) {
                highestReward = innerPacmanMove.reward;
                bestMove = move;
            }

            if (containPill) {
                pills.add(newPacmanNodeIndex);
            }
            visited.remove(newPacmanNodeIndex);
        }
        return new MyMove(highestReward, bestMove);
    }

    MyMove getGhostMove(Game game, MyGhost ghost, int pacmanIndex) {
        Constants.MOVE[] moves = game.getPossibleMoves(ghost.index);
        int shortestDistance = Integer.MAX_VALUE;
        Constants.MOVE bestMove = Constants.MOVE.NEUTRAL;
        for (Constants.MOVE move: moves) {
            int ghostNewIndex = game.getNeighbour(ghost.index, move);
            int distance = game.getShortestPathDistance(ghostNewIndex, pacmanIndex);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestMove = move;
            }
        }
        double reward = -shortestDistance;
        if (shortestDistance < 5) {
            reward = Integer.MAX_VALUE;
        }
        return new MyMove(reward, bestMove);
    }
}
