// 
// Decompiled by Procyon v0.5.36
// 

package pacman.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KeyBoardInput extends KeyAdapter
{
    private int key;
    
    public int getKey() {
        return this.key;
    }
    
    @Override
    public void keyTyped(final KeyEvent e) {
        System.out.println("Typed: " + e.getKeyChar());
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        this.key = e.getKeyCode();
    }
}
