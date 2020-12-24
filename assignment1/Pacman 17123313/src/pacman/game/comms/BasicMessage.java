// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.comms;

import pacman.game.Constants;

public final class BasicMessage implements Message
{
    private final Constants.GHOST sender;
    private final Constants.GHOST recipient;
    private final MessageType type;
    private final int data;
    private final int tick;
    
    public BasicMessage(final Constants.GHOST sender, final Constants.GHOST recipient, final MessageType type, final int data, final int tick) {
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.data = data;
        this.tick = tick;
    }
    
    @Override
    public Constants.GHOST getSender() {
        return this.sender;
    }
    
    @Override
    public Constants.GHOST getRecipient() {
        return this.recipient;
    }
    
    @Override
    public MessageType getType() {
        return this.type;
    }
    
    @Override
    public int getData() {
        return this.data;
    }
    
    @Override
    public int getTick() {
        return this.tick;
    }
}
