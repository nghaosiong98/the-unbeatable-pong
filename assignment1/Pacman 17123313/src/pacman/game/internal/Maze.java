// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import pacman.game.Constants;
import static pacman.game.Constants.pathMazes;

public final class Maze
{
    public AStar astar;
    public int[] shortestPathDistances;
    public int[] pillIndices;
    public int[] powerPillIndices;
    public int[] junctionIndices;
    public int initialPacManNodeIndex;
    public int lairNodeIndex;
    public int initialGhostNodeIndex;
    public Node[] graph;
    public String name;
    
    public Maze(final int index) throws IOException {
        this.loadNodes(Constants.nodeNames[index]);
        this.loadDistances(Constants.distNames[index]);
        (this.astar = new AStar()).createGraph(this.graph);
    }
    
    private void loadNodes(final String fileName) throws FileNotFoundException, IOException {
//        
//        File f = new File("C:\\Users\\ultra\\OneDrive\\Documents\\NetBeansProjects\\pacman\\data\\mazes\\a.txt");
//        if(f.exists() && !f.isDirectory()) { 
            
//        }

        final Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/data/mazes/" + fileName + ".txt"));
//        final Scanner scanner = new Scanner(new FileInputStream(pathMazes+System.getProperty("file.separator")+fileName+".txt"));
//        
//        System.out.println(input);
//        final Scanner scanner = new Scanner(new FileInputStream("C:\\Users\\ultra\\OneDrive\\Documents\\NetBeansProjects\\pacman\\data\\mazes\\" + fileName + ".txt"));

        //System.out.println(this.getClass().getClassLoader().getResourceAsStream("C:\\Users\\ultra\\OneDrive\\Documents\\NetBeansProjects\\pacmandata/mazes/" + fileName + ".txt"));
//        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(pathMazes+System.getProperty("file.separator")+fileName+".txt")));	 
//        String input=br.readLine();

//        File text = new File("C:\\Users\\ultra\\OneDrive\\Documents\\NetBeansProjects\\pacman\\data\\mazes\\" + fileName + ".txt");

//        File text = new File("data/mazes/" + fileName + ".txt");

//        final Scanner scanner = new Scanner(text);
       // final Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/data/mazes/" + fileName + ".txt"));
        String input = scanner.nextLine();

        
        
        final String[] pr = input.split("\t");
        this.name = pr[0];
        this.initialPacManNodeIndex = Integer.parseInt(pr[1]);
        this.lairNodeIndex = Integer.parseInt(pr[2]);
        this.initialGhostNodeIndex = Integer.parseInt(pr[3]);
        this.graph = new Node[Integer.parseInt(pr[4])];
        this.pillIndices = new int[Integer.parseInt(pr[5])];
        this.powerPillIndices = new int[Integer.parseInt(pr[6])];
        this.junctionIndices = new int[Integer.parseInt(pr[7])];
        int nodeIndex = 0;
        int pillIndex = 0;
        int powerPillIndex = 0;
        int junctionIndex = 0;
        while (scanner.hasNextLine()) {
            input = scanner.nextLine();
            final String[] nd = input.split("\t");
            final Node node = new Node(Integer.parseInt(nd[0]), Integer.parseInt(nd[1]), Integer.parseInt(nd[2]), Integer.parseInt(nd[7]), Integer.parseInt(nd[8]), new int[] { Integer.parseInt(nd[3]), Integer.parseInt(nd[4]), Integer.parseInt(nd[5]), Integer.parseInt(nd[6]) });
            this.graph[nodeIndex++] = node;
            if (node.pillIndex >= 0) {
                this.pillIndices[pillIndex++] = node.nodeIndex;
            }
            else if (node.powerPillIndex >= 0) {
                this.powerPillIndices[powerPillIndex++] = node.nodeIndex;
            }
            if (node.numNeighbouringNodes > 2) {
                this.junctionIndices[junctionIndex++] = node.nodeIndex;
            }
        }
        scanner.close();
    }
    
    private void loadDistances(final String fileName) {
        this.shortestPathDistances = new int[this.graph.length * (this.graph.length - 1) / 2 + this.graph.length];
        final Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/data/distances/" + fileName));
        int index = 0;
        while (scanner.hasNextLine()) {
            final String input = scanner.nextLine();
            this.shortestPathDistances[index++] = Integer.parseInt(input);
        }
        scanner.close();
    }
}
