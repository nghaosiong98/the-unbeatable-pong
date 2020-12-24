// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers;

import pacman.game.Game;

public abstract class Controller<T> implements Runnable
{
    protected T lastMove;
    private boolean alive;
    private boolean wasSignalled;
    private boolean hasComputed;
    private volatile boolean threadStillRunning;
    private long timeDue;
    private Game game;
    private String name;
    
    public Controller() {
        this.name = "Unknown Controller";
        this.alive = true;
        this.wasSignalled = false;
        this.hasComputed = false;
        this.threadStillRunning = false;
    }
    
    public final void terminate() {
        this.alive = false;
        this.wasSignalled = true;
        synchronized (this) {
            this.notify();
        }
    }
    
    public final void update(final Game game, final long timeDue) {
        synchronized (this) {
            this.game = game;
            this.timeDue = timeDue;
            this.wasSignalled = true;
            this.hasComputed = false;
            this.notify();
        }
    }
    
    public final T getMove() {
        return this.lastMove;
    }
    
    @Override
    public final void run() {
        while (this.alive) {
            synchronized (this) {
                while (!this.wasSignalled) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!this.threadStillRunning) {
                    new Thread() {
                        @Override
                        public void run() {
                            Controller.this.threadStillRunning = true;
                            Controller.this.lastMove = Controller.this.getMove(Controller.this.game, Controller.this.timeDue);
                            Controller.this.hasComputed = true;
                            Controller.this.threadStillRunning = false;
                        }
                    }.start();
                }
                this.wasSignalled = false;
            }
        }
    }
    
    public final boolean hasComputed() {
        return this.hasComputed;
    }
    
    public abstract T getMove(final Game p0, final long p1);
    
    public String getName() {
        return this.name;
    }
    
    public final void setName(final String name) {
        this.name = name;
    }
}
