// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.comms;

import pacman.game.Constants;

public interface Message
{
    Constants.GHOST getSender();
    
    Constants.GHOST getRecipient();
    
    MessageType getType();
    
    int getData();
    
    int getTick();
    
    public enum MessageType
    {
        PACMAN_SEEN(2), 
        I_AM(1), 
        I_AM_HEADING(1);
        
        private int delay;
        
        private MessageType(final int delay) {
            this.delay = delay;
        }
        
        public int getDelay() {
            return this.delay;
        }
    }
}
