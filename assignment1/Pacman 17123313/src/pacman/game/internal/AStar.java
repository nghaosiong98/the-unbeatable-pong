// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.PriorityQueue;
import pacman.game.Game;
import java.util.EnumMap;
import pacman.game.Constants;

public class AStar
{
    private N[] graph;
    
    public void createGraph(final Node[] nodes) {
        this.graph = new N[nodes.length];
        for (int i = 0; i < nodes.length; ++i) {
            this.graph[i] = new N(nodes[i].nodeIndex);
        }
        for (int i = 0; i < nodes.length; ++i) {
            final EnumMap<Constants.MOVE, Integer> neighbours = nodes[i].neighbourhood;
            final Constants.MOVE[] moves = Constants.MOVE.values();
            for (int j = 0; j < moves.length; ++j) {
                if (neighbours.containsKey(moves[j])) {
                    this.graph[i].adj.add(new E(this.graph[neighbours.get(moves[j])], moves[j], 1.0));
                }
            }
        }
    }
    
    public synchronized int[] computePathsAStar(final int s, final int t, final Constants.MOVE lastMoveMade, final Game game) {
        final N start = this.graph[s];
        final N target = this.graph[t];
        final PriorityQueue<N> open = new PriorityQueue<N>();
        final ArrayList<N> closed = new ArrayList<N>();
        start.g = 0.0;
        start.h = game.getShortestPathDistance(start.index, target.index);
        start.reached = lastMoveMade;
        open.add(start);
        while (!open.isEmpty()) {
            final N currentNode = open.poll();
            closed.add(currentNode);
            if (currentNode.isEqual(target)) {
                break;
            }
            for (final E next : currentNode.adj) {
                if (next.move != currentNode.reached.opposite()) {
                    final double currentDistance = next.cost;
                    if (!open.contains(next.node) && !closed.contains(next.node)) {
                        next.node.g = currentDistance + currentNode.g;
                        next.node.h = game.getShortestPathDistance(next.node.index, target.index);
                        next.node.parent = currentNode;
                        next.node.reached = next.move;
                        open.add(next.node);
                    }
                    else {
                        if (currentDistance + currentNode.g >= next.node.g) {
                            continue;
                        }
                        next.node.g = currentDistance + currentNode.g;
                        next.node.parent = currentNode;
                        next.node.reached = next.move;
                        if (open.contains(next.node)) {
                            open.remove(next.node);
                        }
                        if (closed.contains(next.node)) {
                            closed.remove(next.node);
                        }
                        open.add(next.node);
                    }
                }
            }
        }
        return this.extractPath(target);
    }
    
    public synchronized int[] computePathsAStar(final int s, final int t, final Game game) {
        return this.computePathsAStar(s, t, Constants.MOVE.NEUTRAL, game);
    }
    
    private synchronized int[] extractPath(final N target) {
        final ArrayList<Integer> route = new ArrayList<Integer>();
        N current = target;
        route.add(current.index);
        while (current.parent != null) {
            route.add(current.parent.index);
            current = current.parent;
        }
        Collections.reverse(route);
        final int[] routeArray = new int[route.size()];
        for (int i = 0; i < routeArray.length; ++i) {
            routeArray[i] = route.get(i);
        }
        return routeArray;
    }
    
    public void resetGraph() {
        for (final N node : this.graph) {
            node.g = 0.0;
            node.h = 0.0;
            node.parent = null;
            node.reached = null;
        }
    }
}
