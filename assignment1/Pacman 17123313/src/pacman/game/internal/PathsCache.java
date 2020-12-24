// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import java.util.Iterator;
import java.util.Set;
import java.util.EnumMap;
import java.util.ArrayList;
import pacman.game.Constants;
import java.util.Arrays;
import pacman.game.Game;
import java.util.HashMap;

public class PathsCache
{
    public HashMap<Integer, Integer> junctionIndexConverter;
    public DNode[] nodes;
    public Junction[] junctions;
    public Game game;
    
    public PathsCache(final int mazeIndex) {
        this.junctionIndexConverter = new HashMap<Integer, Integer>();
        this.game = new Game(0L, mazeIndex);
        final Maze m = this.game.getCurrentMaze();
        final int[] jctIndices = m.junctionIndices;
        for (int i = 0; i < jctIndices.length; ++i) {
            this.junctionIndexConverter.put(jctIndices[i], i);
        }
        this.nodes = this.assignJunctionsToNodes(this.game);
        this.junctions = this.junctionDistances(this.game);
        for (int i = 0; i < this.junctions.length; ++i) {
            this.junctions[i].computeShortestPaths();
        }
    }
    
    public int[] getPathFromA2B(final int a, final int b) {
        if (a == b) {
            return new int[0];
        }
        final ArrayList<JunctionData> closestFromJunctions = this.nodes[a].closestJunctions;
        for (int w = 0; w < closestFromJunctions.size(); ++w) {
            for (int i = 0; i < closestFromJunctions.get(w).path.length; ++i) {
                if (closestFromJunctions.get(w).path[i] == b) {
                    return Arrays.copyOf(closestFromJunctions.get(w).path, i + 1);
                }
            }
        }
        final ArrayList<JunctionData> closestToJunctions = this.nodes[b].closestJunctions;
        int minFrom = -1;
        int minTo = -1;
        int minDistance = Integer.MAX_VALUE;
        int[] shortestPath = null;
        for (int j = 0; j < closestFromJunctions.size(); ++j) {
            for (int k = 0; k < closestToJunctions.size(); ++k) {
                int distance = closestFromJunctions.get(j).path.length;
                final int[] tmpPath = this.junctions[this.junctionIndexConverter.get(closestFromJunctions.get(j).nodeID)].paths[this.junctionIndexConverter.get(closestToJunctions.get(k).nodeID)].get(Constants.MOVE.NEUTRAL);
                distance += tmpPath.length;
                distance += closestToJunctions.get(k).path.length;
                if (distance < minDistance) {
                    minDistance = distance;
                    minFrom = j;
                    minTo = k;
                    shortestPath = tmpPath;
                }
            }
        }
        return this.concat(new int[][] { closestFromJunctions.get(minFrom).path, shortestPath, closestToJunctions.get(minTo).reversePath });
    }
    
    public int getPathDistanceFromA2B(final int a, final int b, final Constants.MOVE lastMoveMade) {
        return this.getPathFromA2B(a, b, lastMoveMade).length;
    }
    
    public int[] getPathFromA2B(final int a, final int b, final Constants.MOVE lastMoveMade) {
        if (a == b) {
            return new int[0];
        }
        final JunctionData fromJunction = this.nodes[a].getNearestJunction(lastMoveMade);
        for (int i = 0; i < fromJunction.path.length; ++i) {
            if (fromJunction.path[i] == b) {
                return Arrays.copyOf(fromJunction.path, i + 1);
            }
        }
        final int junctionFrom = fromJunction.nodeID;
        final int junctionFromId = this.junctionIndexConverter.get(junctionFrom);
        final Constants.MOVE moveEnteredJunction = fromJunction.lastMove.equals(Constants.MOVE.NEUTRAL) ? lastMoveMade : fromJunction.lastMove;
        final ArrayList<JunctionData> junctionsTo = this.nodes[b].closestJunctions;
        int minDist = Integer.MAX_VALUE;
        int[] shortestPath = null;
        int closestJunction = -1;
        boolean onTheWay = false;
        for (int q = 0; q < junctionsTo.size(); ++q) {
            final int junctionToId = this.junctionIndexConverter.get(junctionsTo.get(q).nodeID);
            if (junctionFromId == junctionToId) {
                if (!this.game.getMoveToMakeToReachDirectNeighbour(junctionFrom, junctionsTo.get(q).reversePath[0]).equals(moveEnteredJunction.opposite())) {
                    final int[] reversepath = junctionsTo.get(q).reversePath;
                    int cutoff = -1;
                    for (int w = 0; w < reversepath.length; ++w) {
                        if (reversepath[w] == b) {
                            cutoff = w;
                        }
                    }
                    shortestPath = Arrays.copyOf(reversepath, cutoff + 1);
                    minDist = shortestPath.length;
                    closestJunction = q;
                    onTheWay = true;
                }
            }
            else {
                final EnumMap<Constants.MOVE, int[]> paths = this.junctions[junctionFromId].paths[junctionToId];
                final Set<Constants.MOVE> set = paths.keySet();
                for (final Constants.MOVE move : set) {
                    if (!move.opposite().equals(moveEnteredJunction) && !move.equals(Constants.MOVE.NEUTRAL)) {
                        final int[] path = paths.get(move);
                        if (path.length + junctionsTo.get(q).path.length >= minDist) {
                            continue;
                        }
                        minDist = path.length + junctionsTo.get(q).path.length;
                        shortestPath = path;
                        closestJunction = q;
                        onTheWay = false;
                    }
                }
            }
        }
        if (!onTheWay) {
            return this.concat(new int[][] { fromJunction.path, shortestPath, junctionsTo.get(closestJunction).reversePath });
        }
        return this.concat(new int[][] { fromJunction.path, shortestPath });
    }
    
    private Junction[] junctionDistances(final Game game) {
        final Maze m = game.getCurrentMaze();
        final int[] indices = m.junctionIndices;
        final Junction[] junctions = new Junction[indices.length];
        for (int q = 0; q < indices.length; ++q) {
            final Constants.MOVE[] possibleMoves = m.graph[indices[q]].allPossibleMoves.get(Constants.MOVE.NEUTRAL);
            junctions[q] = new Junction(q, indices[q], indices.length);
            for (int z = 0; z < indices.length; ++z) {
                for (int i = 0; i < possibleMoves.length; ++i) {
                    final int neighbour = game.getNeighbour(indices[q], possibleMoves[i]);
                    final int[] p = m.astar.computePathsAStar(neighbour, indices[z], possibleMoves[i], game);
                    m.astar.resetGraph();
                    junctions[q].addPath(z, possibleMoves[i], p);
                }
            }
        }
        return junctions;
    }
    
    private DNode[] assignJunctionsToNodes(final Game game) {
        final Maze m = game.getCurrentMaze();
        final int numNodes = m.graph.length;
        final DNode[] allNodes = new DNode[numNodes];
        for (int i = 0; i < numNodes; ++i) {
            final boolean isJunction = game.isJunction(i);
            allNodes[i] = new DNode(i, isJunction);
            if (!isJunction) {
                final Constants.MOVE[] possibleMoves = m.graph[i].allPossibleMoves.get(Constants.MOVE.NEUTRAL);
                for (int j = 0; j < possibleMoves.length; ++j) {
                    final ArrayList<Integer> path = new ArrayList<Integer>();
                    Constants.MOVE lastMove = possibleMoves[j];
                    int currentNode = game.getNeighbour(i, lastMove);
                    path.add(currentNode);
                    while (!game.isJunction(currentNode)) {
                        final Constants.MOVE[] newPossibleMoves = game.getPossibleMoves(currentNode);
                        for (int q = 0; q < newPossibleMoves.length; ++q) {
                            if (newPossibleMoves[q].opposite() != lastMove) {
                                lastMove = newPossibleMoves[q];
                                break;
                            }
                        }
                        currentNode = game.getNeighbour(currentNode, lastMove);
                        path.add(currentNode);
                    }
                    final int[] array = new int[path.size()];
                    for (int w = 0; w < path.size(); ++w) {
                        array[w] = path.get(w);
                    }
                    allNodes[i].addPath(array[array.length - 1], possibleMoves[j], i, array, lastMove);
                }
            }
        }
        return allNodes;
    }
    
    private int[] concat(final int[]... arrays) {
        int totalLength = 0;
        for (int i = 0; i < arrays.length; ++i) {
            totalLength += arrays[i].length;
        }
        final int[] fullArray = new int[totalLength];
        int index = 0;
        for (int j = 0; j < arrays.length; ++j) {
            for (int k = 0; k < arrays[j].length; ++k) {
                fullArray[index++] = arrays[j][k];
            }
        }
        return fullArray;
    }
}
