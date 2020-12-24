// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game;

import java.io.InputStream;
import java.util.EnumMap;
import java.awt.Toolkit;
import java.awt.Component;
import javax.swing.JFrame;
import java.awt.Composite;
import java.awt.AlphaComposite;
import pacman.game.internal.PacMan;
import pacman.game.internal.Node;
import java.awt.Dimension;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.JComponent;

public final class GameView extends JComponent
{
    public static Vector<DebugPointer> debugPointers;
    public static Vector<DebugLine> debugLines;
    private static boolean isVisible;
    private static boolean saveImage;
    private static String imageFileName;
    private final transient Game game;
    private Images images;
    private Constants.MOVE lastPacManMove;
    private int time;
    private GameFrame frame;
    private Graphics bufferGraphics;
    private BufferedImage offscreen;
    private boolean isPO;
    private Constants.GHOST ghost;
    private double scaleFactor;
    private boolean exitOnClose;
    private Color[] redAlphas;
    private int predictionTicks;
    
    public GameView(final Game game) {
        this(game, true);
    }
    
    public GameView(final Game game, final boolean exitOnClose) {
        this.isPO = false;
        this.ghost = null;
        this.scaleFactor = 1.0;
        this.exitOnClose = false;
        this.game = game;
        this.images = new Images();
        this.lastPacManMove = game.getPacmanLastMoveMade();
        this.time = game.getTotalTime();
        this.redAlphas = new Color[256];
        for (int i = 0; i < 256; ++i) {
            this.redAlphas[i] = new Color(255, 0, 0, i);
        }
        this.exitOnClose = exitOnClose;
    }
    
    public static synchronized void addPoints(final Game game, final Color color, final int... nodeIndices) {
        if (GameView.isVisible) {
            for (int i = 0; i < nodeIndices.length; ++i) {
                GameView.debugPointers.add(new DebugPointer(game.getNodeXCood(nodeIndices[i]), game.getNodeYCood(nodeIndices[i]), color));
            }
        }
    }
    
    public static synchronized void addLines(final Game game, final Color color, final int[] fromNnodeIndices, final int[] toNodeIndices) {
        if (GameView.isVisible) {
            for (int i = 0; i < fromNnodeIndices.length; ++i) {
                GameView.debugLines.add(new DebugLine(game.getNodeXCood(fromNnodeIndices[i]), game.getNodeYCood(fromNnodeIndices[i]), game.getNodeXCood(toNodeIndices[i]), game.getNodeYCood(toNodeIndices[i]), color));
            }
        }
    }
    
    public static synchronized void addLines(final Game game, final Color color, final int fromNnodeIndex, final int toNodeIndex) {
        if (GameView.isVisible) {
            GameView.debugLines.add(new DebugLine(game.getNodeXCood(fromNnodeIndex), game.getNodeYCood(fromNnodeIndex), game.getNodeXCood(toNodeIndex), game.getNodeYCood(toNodeIndex), color));
        }
    }
    
    public static synchronized void saveImage(final String fileName) {
        GameView.saveImage = true;
        GameView.imageFileName = fileName;
    }
    
    private void drawDebugInfo() {
        for (int i = 0; i < GameView.debugPointers.size(); ++i) {
            final DebugPointer dp = GameView.debugPointers.get(i);
            this.bufferGraphics.setColor(dp.color);
            this.bufferGraphics.fillRect(dp.x * 2 + 1, dp.y * 2 + 5, 10, 10);
        }
        for (int i = 0; i < GameView.debugLines.size(); ++i) {
            final DebugLine dl = GameView.debugLines.get(i);
            this.bufferGraphics.setColor(dl.color);
            this.bufferGraphics.drawLine(dl.x1 * 2 + 5, dl.y1 * 2 + 10, dl.x2 * 2 + 5, dl.y2 * 2 + 10);
        }
        GameView.debugPointers.clear();
        GameView.debugLines.clear();
    }
    
    private void saveImage() {
        try {
            ImageIO.write(this.offscreen, "png", new File("myData/" + GameView.imageFileName + ".png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GameView.saveImage = false;
    }
    
    public void paintComponent(final Graphics g) {
        this.time = this.game.getTotalTime();
        final Graphics2D g2 = (Graphics2D)g;
        g2.scale(this.scaleFactor, this.scaleFactor);
        if (this.offscreen == null) {
            this.offscreen = (BufferedImage)this.createImage(this.getPreferredSize().width, this.getPreferredSize().height);
            this.bufferGraphics = this.offscreen.getGraphics();
        }
        this.drawMaze();
        this.drawDebugInfo();
        this.drawPills();
        this.drawPowerPills();
        this.drawPacMan();
        this.drawGhosts();
        this.drawLives();
        this.drawGameInfo();
        if (this.isPO) {
            if (this.ghost == null) {
                this.drawPacManVisibility();
            }
            else {
                this.drawGhostVisibility(this.ghost);
            }
        }
        if (this.game.gameOver()) {
            this.drawGameOver();
        }
        g.drawImage(this.offscreen, 0, 0, this);
        if (GameView.saveImage) {
            this.saveImage();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int)(228.0 * this.scaleFactor), (int)(260.0 * this.scaleFactor) + 20);
    }
    
    private void drawMaze() {
        this.bufferGraphics.setColor(Color.BLACK);
        this.bufferGraphics.fillRect(0, 0, 228, 280);
        this.bufferGraphics.drawImage(this.images.getMaze(this.game.getMazeIndex()), 2, 6, null);
    }
    
    private void drawPills() {
        final int[] pillIndices = this.game.getPillIndices();
        this.bufferGraphics.setColor(Color.white);
        for (int i = 0; i < pillIndices.length; ++i) {
            if (this.game.isPillStillAvailable(i)) {
                this.bufferGraphics.fillOval(this.game.getNodeXCood(pillIndices[i]) * 2 + 4, this.game.getNodeYCood(pillIndices[i]) * 2 + 8, 3, 3);
            }
        }
    }
    
    private void drawPowerPills() {
        final int[] powerPillIndices = this.game.getPowerPillIndices();
        this.bufferGraphics.setColor(Color.white);
        for (int i = 0; i < powerPillIndices.length; ++i) {
            if (this.game.isPowerPillStillAvailable(i)) {
                this.bufferGraphics.fillOval(this.game.getNodeXCood(powerPillIndices[i]) * 2 + 1, this.game.getNodeYCood(powerPillIndices[i]) * 2 + 5, 8, 8);
            }
        }
    }
    
    private void drawPacMan() {
        final int pacLoc = this.game.getPacmanCurrentNodeIndex();
        final Constants.MOVE tmpLastPacManMove = this.game.getPacmanLastMoveMade();
        if (tmpLastPacManMove != Constants.MOVE.NEUTRAL) {
            this.lastPacManMove = tmpLastPacManMove;
        }
        this.bufferGraphics.drawImage(this.images.getPacMan(this.lastPacManMove, this.time), this.game.getNodeXCood(pacLoc) * 2 - 1, this.game.getNodeYCood(pacLoc) * 2 + 3, null);
    }
    
    private void drawNodes() {
        this.bufferGraphics.setColor(Color.CYAN);
        for (final Node node : this.game.getCurrentMaze().graph) {
            this.bufferGraphics.drawRect(node.x * 2 - 1, node.y * 2 + 3, 1, 1);
        }
    }
    
    private void drawPacManVisibility() {
        final Game pacmanGame = this.game.copy(new PacMan(0, this.game.getPacmanLastMoveMade(), this.game.getPacmanNumberOfLivesRemaining(), false));
        final int pacmanLocation = this.game.getPacmanCurrentNodeIndex();
        this.drawVisibility(pacmanLocation, pacmanGame);
    }
    
    private void drawGhostVisibility(final Constants.GHOST ghost) {
        final Game ghostGame = this.game.copy(ghost);
        final int ghostLocation = this.game.getGhostCurrentNodeIndex(ghost);
        this.drawVisibility(ghostLocation, ghostGame);
    }
    
    private void drawVisibility(final int location, final Game pacmanGame) {
        final BufferedImage image = new BufferedImage(228, 260, 6);
        final Graphics2D overlay = (Graphics2D)image.getGraphics();
        overlay.setColor(Color.GRAY);
        for (int i = 0; i < this.game.getNumberOfNodes(); ++i) {
            if (!pacmanGame.isNodeObservable(i)) {
                overlay.fillRect(this.game.getNodeXCood(i) * 2 - 1, this.game.getNodeYCood(i) * 2 + 3, 14, 14);
            }
        }
        overlay.setColor(Color.WHITE);
        overlay.setComposite(AlphaComposite.Clear);
        int totalVisisble = 0;
        for (final Constants.MOVE move : Constants.MOVE.values()) {
            for (int nextPoint = location; pacmanGame.isNodeObservable(nextPoint); nextPoint = this.game.getNeighbour(nextPoint, move)) {
                overlay.fillRect(this.game.getNodeXCood(nextPoint) * 2 - 1, this.game.getNodeYCood(nextPoint) * 2 + 3, 14, 14);
                ++totalVisisble;
            }
        }
        this.bufferGraphics.drawImage(image, 0, 0, null);
    }
    
    private void drawGhosts() {
        for (final Constants.GHOST ghostType : Constants.GHOST.values()) {
            final int currentNodeIndex = this.game.getGhostCurrentNodeIndex(ghostType);
            final int nodeXCood = this.game.getNodeXCood(currentNodeIndex);
            final int nodeYCood = this.game.getNodeYCood(currentNodeIndex);
            if (this.game.getGhostEdibleTime(ghostType) > 0) {
                if (this.game.getGhostEdibleTime(ghostType) < 30 && this.time % 6 / 3 == 0) {
                    this.bufferGraphics.drawImage(this.images.getEdibleGhost(true, this.time), nodeXCood * 2 - 1, nodeYCood * 2 + 3, null);
                }
                else {
                    this.bufferGraphics.drawImage(this.images.getEdibleGhost(false, this.time), nodeXCood * 2 - 1, nodeYCood * 2 + 3, null);
                }
            }
            else {
                final int index = ghostType.ordinal();
                if (this.game.getGhostLairTime(ghostType) > 0) {
                    this.bufferGraphics.drawImage(this.images.getGhost(ghostType, this.game.getGhostLastMoveMade(ghostType), this.time), nodeXCood * 2 - 1 + index * 5, nodeYCood * 2 + 3, null);
                }
                else {
                    this.bufferGraphics.drawImage(this.images.getGhost(ghostType, this.game.getGhostLastMoveMade(ghostType), this.time), nodeXCood * 2 - 1, nodeYCood * 2 + 3, null);
                }
            }
        }
    }
    
    private void drawLives() {
        for (int i = 0; i < this.game.getPacmanNumberOfLivesRemaining() - 1; ++i) {
            this.bufferGraphics.drawImage(this.images.getPacManForExtraLives(), 210 - 30 * i / 2, 260, null);
        }
    }
    
    private void drawGameInfo() {
        this.bufferGraphics.setColor(Color.WHITE);
        this.bufferGraphics.drawString("S: ", 4, 271);
        this.bufferGraphics.drawString(Integer.toString(this.game.getScore()), 16, 271);
        this.bufferGraphics.drawString("L: ", 78, 271);
        this.bufferGraphics.drawString(Integer.toString(this.game.getCurrentLevel() + 1), 90, 271);
        this.bufferGraphics.drawString("T: ", 116, 271);
        this.bufferGraphics.drawString(Integer.toString(this.game.getCurrentLevelTime()), 129, 271);
    }
    
    private void drawGameOver() {
        this.bufferGraphics.setColor(Color.WHITE);
        this.bufferGraphics.drawString("Game Over", 80, 150);
    }
    
    public GameView showGame() {
        this.frame = new GameFrame(this);
        try {
            Thread.sleep(1000L);
        }
        catch (Exception ex) {}
        return this;
    }
    
    public GameFrame getFrame() {
        return this.frame;
    }
    
    public void setPO(final boolean po) {
        this.isPO = po;
        this.ghost = null;
    }
    
    public void setPO(final boolean po, final Constants.GHOST ghost) {
        this.isPO = po;
        this.ghost = ghost;
    }
    
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    static {
        GameView.debugPointers = new Vector<DebugPointer>();
        GameView.debugLines = new Vector<DebugLine>();
        GameView.isVisible = true;
        GameView.saveImage = false;
        GameView.imageFileName = "";
    }
    
    private static class DebugPointer
    {
        public int x;
        public int y;
        public Color color;
        
        public DebugPointer(final int x, final int y, final Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
    
    private static class DebugLine
    {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public Color color;
        
        public DebugLine(final int x1, final int y1, final int x2, final int y2, final Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }
    
    public class GameFrame extends JFrame
    {
        public GameFrame(final JComponent comp) {
            this.getContentPane().add("Center", comp);
            this.pack();
            final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((int)(screen.getWidth() * 3.0 / 8.0), (int)(screen.getHeight() * 3.0 / 8.0));
            this.setVisible(true);
            this.setResizable(false);
            this.setDefaultCloseOperation(GameView.this.exitOnClose ? 3 : 2);
            this.repaint();
        }
    }
    
    public class Images
    {
        private EnumMap<Constants.MOVE, BufferedImage[]> pacman;
        private EnumMap<Constants.GHOST, EnumMap<Constants.MOVE, BufferedImage[]>> ghosts;
        private BufferedImage[] edibleGhosts;
        private BufferedImage[] edibleBlinkingGhosts;
        private BufferedImage[] mazes;
        
        public Images() {
            (this.pacman = new EnumMap<Constants.MOVE, BufferedImage[]>(Constants.MOVE.class)).put(Constants.MOVE.UP, new BufferedImage[] { this._loadImage("mspacman-up-normal.png"), this._loadImage("mspacman-up-open.png"), this._loadImage("mspacman-up-closed.png") });
            this.pacman.put(Constants.MOVE.RIGHT, new BufferedImage[] { this._loadImage("mspacman-right-normal.png"), this._loadImage("mspacman-right-open.png"), this._loadImage("mspacman-right-closed.png") });
            this.pacman.put(Constants.MOVE.DOWN, new BufferedImage[] { this._loadImage("mspacman-down-normal.png"), this._loadImage("mspacman-down-open.png"), this._loadImage("mspacman-down-closed.png") });
            this.pacman.put(Constants.MOVE.LEFT, new BufferedImage[] { this._loadImage("mspacman-left-normal.png"), this._loadImage("mspacman-left-open.png"), this._loadImage("mspacman-left-closed.png") });
            (this.ghosts = new EnumMap<Constants.GHOST, EnumMap<Constants.MOVE, BufferedImage[]>>(Constants.GHOST.class)).put(Constants.GHOST.BLINKY, new EnumMap<Constants.MOVE, BufferedImage[]>(Constants.MOVE.class));
            this.ghosts.get(Constants.GHOST.BLINKY).put(Constants.MOVE.UP, new BufferedImage[] { this._loadImage("blinky-up-1.png"), this._loadImage("blinky-up-2.png") });
            this.ghosts.get(Constants.GHOST.BLINKY).put(Constants.MOVE.RIGHT, new BufferedImage[] { this._loadImage("blinky-right-1.png"), this._loadImage("blinky-right-2.png") });
            this.ghosts.get(Constants.GHOST.BLINKY).put(Constants.MOVE.DOWN, new BufferedImage[] { this._loadImage("blinky-down-1.png"), this._loadImage("blinky-down-2.png") });
            this.ghosts.get(Constants.GHOST.BLINKY).put(Constants.MOVE.LEFT, new BufferedImage[] { this._loadImage("blinky-left-1.png"), this._loadImage("blinky-left-2.png") });
            this.ghosts.put(Constants.GHOST.PINKY, new EnumMap<Constants.MOVE, BufferedImage[]>(Constants.MOVE.class));
            this.ghosts.get(Constants.GHOST.PINKY).put(Constants.MOVE.UP, new BufferedImage[] { this._loadImage("pinky-up-1.png"), this._loadImage("pinky-up-2.png") });
            this.ghosts.get(Constants.GHOST.PINKY).put(Constants.MOVE.RIGHT, new BufferedImage[] { this._loadImage("pinky-right-1.png"), this._loadImage("pinky-right-2.png") });
            this.ghosts.get(Constants.GHOST.PINKY).put(Constants.MOVE.DOWN, new BufferedImage[] { this._loadImage("pinky-down-1.png"), this._loadImage("pinky-down-2.png") });
            this.ghosts.get(Constants.GHOST.PINKY).put(Constants.MOVE.LEFT, new BufferedImage[] { this._loadImage("pinky-left-1.png"), this._loadImage("pinky-left-2.png") });
            this.ghosts.put(Constants.GHOST.INKY, new EnumMap<Constants.MOVE, BufferedImage[]>(Constants.MOVE.class));
            this.ghosts.get(Constants.GHOST.INKY).put(Constants.MOVE.UP, new BufferedImage[] { this._loadImage("inky-up-1.png"), this._loadImage("inky-up-2.png") });
            this.ghosts.get(Constants.GHOST.INKY).put(Constants.MOVE.RIGHT, new BufferedImage[] { this._loadImage("inky-right-1.png"), this._loadImage("inky-right-2.png") });
            this.ghosts.get(Constants.GHOST.INKY).put(Constants.MOVE.DOWN, new BufferedImage[] { this._loadImage("inky-down-1.png"), this._loadImage("inky-down-2.png") });
            this.ghosts.get(Constants.GHOST.INKY).put(Constants.MOVE.LEFT, new BufferedImage[] { this._loadImage("inky-left-1.png"), this._loadImage("inky-left-2.png") });
            this.ghosts.put(Constants.GHOST.SUE, new EnumMap<Constants.MOVE, BufferedImage[]>(Constants.MOVE.class));
            this.ghosts.get(Constants.GHOST.SUE).put(Constants.MOVE.UP, new BufferedImage[] { this._loadImage("sue-up-1.png"), this._loadImage("sue-up-2.png") });
            this.ghosts.get(Constants.GHOST.SUE).put(Constants.MOVE.RIGHT, new BufferedImage[] { this._loadImage("sue-right-1.png"), this._loadImage("sue-right-2.png") });
            this.ghosts.get(Constants.GHOST.SUE).put(Constants.MOVE.DOWN, new BufferedImage[] { this._loadImage("sue-down-1.png"), this._loadImage("sue-down-2.png") });
            this.ghosts.get(Constants.GHOST.SUE).put(Constants.MOVE.LEFT, new BufferedImage[] { this._loadImage("sue-left-1.png"), this._loadImage("sue-left-2.png") });
            (this.edibleGhosts = new BufferedImage[2])[0] = this._loadImage("edible-ghost-1.png");
            this.edibleGhosts[1] = this._loadImage("edible-ghost-2.png");
            (this.edibleBlinkingGhosts = new BufferedImage[2])[0] = this._loadImage("edible-ghost-blink-1.png");
            this.edibleBlinkingGhosts[1] = this._loadImage("edible-ghost-blink-2.png");
            this.mazes = new BufferedImage[4];
            for (int i = 0; i < this.mazes.length; ++i) {
                this.mazes[i] = this._loadImage(Constants.mazeNames[i]);
            }
        }
        
        public BufferedImage getPacMan(final Constants.MOVE move, final int time) {
            return this.pacman.get(move)[time % 6 / 2];
        }
        
        public BufferedImage getPacManForExtraLives() {
            return this.pacman.get(Constants.MOVE.RIGHT)[0];
        }
        
        public BufferedImage getGhost(final Constants.GHOST ghost, final Constants.MOVE move, final int time) {
            if (move == Constants.MOVE.NEUTRAL) {
                return this.ghosts.get(ghost).get(Constants.MOVE.UP)[time % 6 / 3];
            }
            return this.ghosts.get(ghost).get(move)[time % 6 / 3];
        }
        
        public BufferedImage getEdibleGhost(final boolean blinking, final int time) {
            if (!blinking) {
                return this.edibleGhosts[time % 6 / 3];
            }
            return this.edibleBlinkingGhosts[time % 6 / 3];
        }
        
        public BufferedImage getMaze(final int mazeIndex) {
            return this.mazes[mazeIndex];
        }
        
        private BufferedImage _loadImage(final String fileName) {
            BufferedImage image = null;
            try {
                final InputStream in = this.getClass().getResourceAsStream(Constants.pathImages + "/" + fileName);
                image = ImageIO.read(in);
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
    }
}
