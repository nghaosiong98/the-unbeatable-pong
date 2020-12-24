// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game;

public final class Constants
{
    public static final int PILL = 10;
    public static final int POWER_PILL = 50;
    public static final int GHOST_EAT_SCORE = 200;
    public static final int EDIBLE_TIME = 200;
    public static final float EDIBLE_TIME_REDUCTION = 0.9f;
    public static final float LAIR_REDUCTION = 0.9f;
    public static final int LEVEL_RESET_REDUCTION = 6;
    public static final int COMMON_LAIR_TIME = 40;
    public static final int LEVEL_LIMIT = 4000;
    public static final float GHOST_REVERSAL = 0.0015f;
    public static final int MAX_TIME = 24000;
    public static final int AWARD_LIFE_LEFT = 800;
    public static final int EXTRA_LIFE_SCORE = 10000;
    public static final int EAT_DISTANCE = 2;
    public static final int NUM_GHOSTS = 4;
    public static final int NUM_MAZES = 4;
    public static final int DELAY = 40;
    public static final int NUM_LIVES = 3;
    public static final int GHOST_SPEED_REDUCTION = 2;
    public static final int EDIBLE_ALERT = 30;
    public static final long INTERVAL_WAIT = 1L;
    public static final int WAIT_LIMIT = 5000;
    public static final int MEMORY_LIMIT = 512;
    public static final int IO_LIMIT = 10;
    public static final String pathMazes = "/data/mazes";
    public static final String pathDistances = "/data/distances";
    public static final String[] nodeNames;
    public static final String[] distNames;
    public static final int MAG = 2;
    public static final int GV_WIDTH = 114;
    public static final int GV_HEIGHT = 130;
    public static String pathImages;
    public static String[] mazeNames;
    
    private Constants() {
    }
    
    static {
        nodeNames = new String[] { "a", "b", "c", "d" };
        distNames = new String[] { "da", "db", "dc", "dd" };
        Constants.pathImages = "/data/images";
        Constants.mazeNames = new String[] { "maze-a.png", "maze-b.png", "maze-c.png", "maze-d.png" };
    }
    
    public enum MOVE
    {
        UP {
            @Override
            public MOVE opposite() {
                return MOVE.DOWN;
            }
        }, 
        RIGHT {
            @Override
            public MOVE opposite() {
                return MOVE.LEFT;
            }
        }, 
        DOWN {
            @Override
            public MOVE opposite() {
                return MOVE.UP;
            }
        }, 
        LEFT {
            @Override
            public MOVE opposite() {
                return MOVE.RIGHT;
            }
        }, 
        NEUTRAL {
            @Override
            public MOVE opposite() {
                return MOVE.NEUTRAL;
            }
        };
        
        public abstract MOVE opposite();
    }
    
    public enum GHOST
    {
        BLINKY(40, "Blinky"), 
        PINKY(60, "Pinky"), 
        INKY(80, "Inky"), 
        SUE(100, "Sue");
        
        public final int initialLairTime;
        public final String className;
        
        private GHOST(final int lairTime, final String className) {
            this.initialLairTime = lairTime;
            this.className = className;
        }
    }
    
    public enum DM
    {
        PATH, 
        EUCLID, 
        MANHATTAN;
    }
}
