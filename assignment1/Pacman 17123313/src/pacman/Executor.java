// 
// Decompiled by Procyon v0.5.36
// 

package pacman;

import pacman.controllers.HumanController;
import pacman.game.GameView;
import pacman.game.Game;
import java.util.Random;
import java.util.EnumMap;
import pacman.game.Constants;
import pacman.controllers.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import pacman.game.util.Stats;
import java.io.FileWriter;
import java.io.IOException;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.MyMonteCarloPacMan;
import pacman.controllers.examples.MyTreeSearchPacMan;
import pacman.controllers.examples.MyRuleBasedPacMan;
import pacman.controllers.examples.StarterPacMan;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.Legacy       ; 
import pacman.game.comms.BasicMessenger;
import pacman.game.comms.Messenger;

public class Executor
{
    protected final boolean pacmanPO;
    protected final boolean ghostsMessage;
    protected boolean ghostsPresent;
    protected boolean pillsPresent;
    protected boolean powerPillsPresent;
    protected Messenger messenger;
    private double scaleFactor;
    private boolean setDaemon;
    
    public Executor() {
        this.ghostsPresent = true;
        this.pillsPresent = true;
        this.powerPillsPresent = true;
        this.scaleFactor = 1.0;
        this.setDaemon = false;
        this.pacmanPO = false;
        this.ghostsMessage = false;
    }
    
    public Executor(final boolean pacmanPO) {
        this.ghostsPresent = true;
        this.pillsPresent = true;
        this.powerPillsPresent = true;
        this.scaleFactor = 1.0;
        this.setDaemon = false;
        this.pacmanPO = pacmanPO;
        this.ghostsMessage = false;
    }
    
    public Executor(final boolean pacmanPO, final boolean ghostsMessage) {
        this.ghostsPresent = true;
        this.pillsPresent = true;
        this.powerPillsPresent = true;
        this.scaleFactor = 1.0;
        this.setDaemon = false;
        this.pacmanPO = pacmanPO;
        this.ghostsMessage = ghostsMessage;
        if (this.ghostsMessage) {
            this.messenger = new BasicMessenger(0, 1, 1);
        }
    }
    
    public static void main(final String[] args) throws IOException {
        
                int delay=5;
		boolean visual=true;
		int numTrials=10;
		
		Executor exec=new Executor();
		
		/* run a game in synchronous mode: game waits until controllers respond. */
	//	System.out.println("STARTER PACMAN vs LEGACY2THERECKONING");
	//	exec.runGame(new MonteCarloPacMan(), new Legacy2TheReckoning(), visual,delay);
                //exec.runGame(new StarterPacMan(), new Legacy2TheReckoning(), visual,delay);
            //    exec.runGame(new MyMonteCarloPacMan(), new Legacy2TheReckoning(), visual,delay);
                //exec.runGame(new MyTreeSearchPacMan(), new Legacy2TheReckoning(), visual,delay);
		//exec.runGame(new MyRuleBasedPacMan(), new Legacy2TheReckoning(), visual,delay);
		
                
                /* run multiple games in batch mode - good for testing. */
		
		System.out.println("MONTE CARLO PACMAN vs Legacy");
		exec.runExperiment(new MyMonteCarloPacMan(), new Legacy(),numTrials,"");
	//	System.out.println("TREE SEARCH PACMAN vs Legacy");
	//	exec.runExperiment(new MyTreeSearchPacMan(), new Legacy(),numTrials,"");
		System.out.println("RULE BASED PACMAN vs Legacy");
		exec.runExperiment(new MyRuleBasedPacMan(), new Legacy(),numTrials,"");
		
//		
//		System.out.println("STARTER PACMAN vs starter GHOSTS");
//		exec.runExperiment(new MonteCarloPacMan(), new StarterGhosts(),numTrials);
//		System.out.println("RANDOM PACMAN vs RANDOM GHOSTS");
//		exec.runExperiment(new RandomPacMan(),  new StarterGhosts(),numTrials);
//		System.out.println("NEAREST PILL PACMAN vs RANDOM GHOSTS");
//		exec.runExperiment(new NearestPillPacMan(), new StarterGhosts(),numTrials);
		

  		 
		
		/* run the game in asynchronous mode. */
		
//		exec.runGameTimed(new MyPacMan(),new AggressiveGhosts(),visual);
//		exec.runGameTimed(new RandomPacMan(), new AvengersEvolution(evolutionFile),visual);
//		exec.runGameTimed(new HumanController(new KeyBoardInput()),new StarterGhosts(),visual);	
		
		
		/* run the game in asynchronous mode but advance as soon as both controllers are ready  - this is the mode of the competition.
		time limit of DELAY ms still applies.*/
		
//		boolean visual=true;
//		boolean fixedTime=false;
//		exec.runGameTimedSpeedOptimised(new MyMCTSPacMan(new AggressiveGhosts()),new AggressiveGhosts(),fixedTime,visual);
	
		
		/* run game in asynchronous mode and record it to file for replay at a later stage. */
		

		//String fileName="replay.txt";
		//exec.runGameTimedRecorded(new HumanController(new KeyBoardInput()),new RandomGhosts(),visual,fileName);
		//exec.replayGame(fileName,visual);
        
        
        
        
        
        
        
        
        
        
        
    }
    
    private static void writeStat(final FileWriter writer, final Stats stat, final int i) throws IOException {
        writer.write(String.format("%s, %d, %f, %f, %f, %f, %d, %f, %f, %f, %d%n", stat.getDescription(), i, stat.getAverage(), stat.getSum(), stat.getSumsq(), stat.getStandardDeviation(), stat.getN(), stat.getMin(), stat.getMax(), stat.getStandardError(), stat.getMsTaken()));
    }
    
    public static void saveToFile(final String data, final String name, final boolean append) {
        try (final FileOutputStream outS = new FileOutputStream(name, append)) {
            final PrintWriter pw = new PrintWriter(outS);
            pw.println(data);
            pw.flush();
            outS.close();
        }
        catch (IOException e) {
            System.out.println("Could not save data!");
        }
    }
    
    private static ArrayList<String> loadReplay(final String fileName) {
        final ArrayList<String> replay = new ArrayList<String>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            for (String input = br.readLine(); input != null; input = br.readLine()) {
                if (!input.equals("")) {
                    replay.add(input);
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return replay;
    }
    
    public void setMessenger(final Messenger messenger) {
        if (this.ghostsMessage && messenger != null) {
            this.messenger = messenger;
        }
    }
    
    public Stats[] runExperiment(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final int trials, final String description, final int tickLimit) {
        final Stats stats = new Stats(description);
        final Stats ticks = new Stats(description + " Ticks");
        final Random rnd = new Random(0L);
        final Long startTime = System.currentTimeMillis();
        int i = 0;
        int total=0;
        while (i < trials) {
            try {
                final Game game = this.ghostsMessage ? new Game(rnd.nextLong(), this.messenger.copy()) : new Game(rnd.nextLong());
                while (!game.gameOver() && (tickLimit == -1 || tickLimit >= game.getCurrentLevelTime())) {
                    game.advanceGame(pacManController.getMove(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), System.currentTimeMillis() + 40L), ghostController.getMove(game.copy(), System.currentTimeMillis() + 40L));
                }
                stats.add(game.getScore());
                ticks.add(game.getCurrentLevelTime());
                ++i;
                //System.out.println("Game finished: " + i + "   " + description);
                System.out.println("Game finished: " + i + "   "+"Score:"+game.getScore());
                total+=game.getScore();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average finished: " + total/trials + "   "+"Total Score:"+ total);
        final long timeTaken = System.currentTimeMillis() - startTime;
        stats.setMsTaken(timeTaken);
        ticks.setMsTaken(timeTaken);
        return new Stats[] { stats, ticks };
    }
    
    public Stats[] runExperiment(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final int trials, final String description) {
        return this.runExperiment(pacManController, ghostController, trials, description, -1);
    }
    
    public Stats[] runExperimentTicks(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final int trials, final String description) {
        final Stats stats = new Stats(description);
        final Stats ticks = new Stats(description);
        final Random rnd = new Random(0L);
        final Long startTime = System.currentTimeMillis();
        for (int i = 0; i < trials; ++i) {
            final Game game = this.ghostsMessage ? new Game(rnd.nextLong(), this.messenger.copy()) : new Game(rnd.nextLong());
            while (!game.gameOver()) {
                game.advanceGame(pacManController.getMove(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), System.currentTimeMillis() + 40L), ghostController.getMove(game.copy(), System.currentTimeMillis() + 40L));
            }
            stats.add(game.getScore());
            ticks.add(game.getTotalTime());
        }
        stats.setMsTaken(System.currentTimeMillis() - startTime);
        ticks.setMsTaken(System.currentTimeMillis() - startTime);
        return new Stats[] { stats, ticks };
    }
    
    public int runGame(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final boolean visual, final int delay) {
        final Game game = this.ghostsMessage ? new Game(0L, this.messenger.copy()) : new Game(0L);
        GameView gv = null;
        if (visual) {
            gv = new GameView(game, this.setDaemon);
            gv.setScaleFactor(this.scaleFactor);
            gv.showGame();
            if (pacManController instanceof HumanController) {
                gv.setFocusable(true);
                gv.requestFocus();
                gv.setPO(true);
                gv.addKeyListener(((HumanController)pacManController).getKeyboardInput());
            }
        }
        while (!game.gameOver()) {
            game.advanceGame(pacManController.getMove(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), -1L), ghostController.getMove(game.copy(), -1L));
            try {
                Thread.sleep(delay);
            }
            catch (Exception ex) {}
            if (visual) {
                gv.repaint();
            }
        }
        System.out.println(game.getScore());
        return game.getScore();
    }
    
    public void runGameTimed(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final boolean visual) {
        final Game game = this.ghostsMessage ? new Game(0L, this.messenger.copy()) : new Game(0L);
        GameView gv = null;
        if (visual) {
            gv = new GameView(game, this.setDaemon);
            gv.setScaleFactor(this.scaleFactor);
            gv.showGame();
        }
        if (gv != null && pacManController instanceof HumanController) {
            gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
        }
        new Thread(pacManController).start();
        new Thread(ghostController).start();
        while (!game.gameOver()) {
            pacManController.update(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), System.currentTimeMillis() + 40L);
            ghostController.update(game.copy(), System.currentTimeMillis() + 40L);
            try {
                Thread.sleep(40L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.advanceGame(pacManController.getMove(), ghostController.getMove());
            if (visual) {
                gv.repaint();
            }
        }
        pacManController.terminate();
        ghostController.terminate();
    }
    
    public Stats runGameTimedSpeedOptimised(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final boolean fixedTime, final boolean visual, final String desc) {
        final Game game = this.ghostsMessage ? new Game(0L, this.messenger.copy()) : new Game(0L);
        GameView gv = null;
        final Stats stats = new Stats(desc);
        if (visual) {
            gv = new GameView(game, this.setDaemon);
            gv.setScaleFactor(this.scaleFactor);
            gv.showGame();
        }
        if (gv != null && pacManController instanceof HumanController) {
            gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
        }
        new Thread(pacManController).start();
        new Thread(ghostController).start();
        int ticks = 0;
        while (!game.gameOver()) {
            pacManController.update(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), System.currentTimeMillis() + 40L);
            ghostController.update(game.copy(), System.currentTimeMillis() + 40L);
            try {
                long waited = 40L;
                for (int j = 0; j < 40L; ++j) {
                    Thread.sleep(1L);
                    if (pacManController.hasComputed() && ghostController.hasComputed()) {
                        waited = j;
                        break;
                    }
                }
                if (fixedTime) {
                    Thread.sleep((40L - waited) * 1L);
                }
                game.advanceGame(pacManController.getMove(), ghostController.getMove());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (visual) {
                gv.repaint();
            }
            if (++ticks > 4000) {
                break;
            }
        }
        pacManController.terminate();
        ghostController.terminate();
        stats.add(game.getScore());
        return stats;
    }
    
    public Stats runGameTimedRecorded(final Controller<Constants.MOVE> pacManController, final Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, final boolean visual, final String fileName) {
        final Stats stats = new Stats("");
        final StringBuilder replay = new StringBuilder();
        final Game game = this.ghostsMessage ? new Game(0L, this.messenger.copy()) : new Game(0L);
        GameView gv = null;
        if (visual) {
            gv = new GameView(game, this.setDaemon);
            gv.setScaleFactor(this.scaleFactor);
            gv.showGame();
            if (pacManController instanceof HumanController) {
                gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
            }
        }
        new Thread(pacManController).start();
        new Thread(ghostController).start();
        while (!game.gameOver()) {
            pacManController.update(game.copy(this.pacmanPO ? (Constants.GHOST.values().length + 1) : -1), System.currentTimeMillis() + 40L);
            ghostController.update(game.copy(), System.currentTimeMillis() + 40L);
            try {
                Thread.sleep(40L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.advanceGame(pacManController.getMove(), ghostController.getMove());
            if (visual) {
                gv.repaint();
            }
            replay.append(game.getGameState() + "\n");
        }
        stats.add(game.getScore());
        pacManController.terminate();
        ghostController.terminate();
        saveToFile(replay.toString(), fileName, false);
        return stats;
    }
    
    public void replayGame(final String fileName, final boolean visual) {
        final ArrayList<String> timeSteps = loadReplay(fileName);
        final Game game = this.ghostsMessage ? new Game(0L, this.messenger.copy()) : new Game(0L);
        GameView gv = null;
        if (visual) {
            gv = new GameView(game, this.setDaemon);
            gv.setScaleFactor(this.scaleFactor);
            gv.showGame();
        }
        for (int j = 0; j < timeSteps.size(); ++j) {
            game.setGameState(timeSteps.get(j));
            try {
                Thread.sleep(40L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (visual) {
                gv.repaint();
            }
        }
    }
    
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    public void setDaemon(final boolean daemon) {
        this.setDaemon = daemon;
    }
}
