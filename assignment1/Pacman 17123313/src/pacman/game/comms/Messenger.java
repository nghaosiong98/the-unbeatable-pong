// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.comms;

import java.util.ArrayList;
import pacman.game.Constants;

public interface Messenger
{
    Messenger copy();
    
    void update();
    
    void addMessage(final BasicMessage p0);
    
    ArrayList<Message> getMessages(final Constants.GHOST p0);
}
