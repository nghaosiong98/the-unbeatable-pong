// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.comms;

import pacman.game.Constants;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

public class BasicMessenger implements Messenger
{
    private Map<Integer, ArrayList<Message>> messages;
    private int currentTick;
    private int delayConstant;
    private int delayMultiplier;
    
    public BasicMessenger() {
        this(0, 1, 1);
    }
    
    public BasicMessenger(final int currentTick, final int delayConstant, final int delayMultiplier) {
        this.messages = new TreeMap<Integer, ArrayList<Message>>();
        this.currentTick = currentTick;
        this.delayConstant = delayConstant;
        this.delayMultiplier = delayMultiplier;
    }
    
    @Override
    public BasicMessenger copy() {
        final BasicMessenger result = new BasicMessenger(this.currentTick, this.delayConstant, this.delayMultiplier);
        for (final Integer key : this.messages.keySet()) {
            result.messages.put(key, (ArrayList<Message>)this.messages.get(key).clone());
        }
        return result;
    }
    
    @Override
    public void update() {
        if (this.messages.containsKey(this.currentTick)) {
            this.messages.remove(this.currentTick);
        }
        ++this.currentTick;
    }
    
    @Override
    public void addMessage(final BasicMessage message) {
        final int tickToDeliver = this.currentTick + this.delayConstant + this.delayMultiplier * message.getType().getDelay();
        if (!this.messages.containsKey(tickToDeliver)) {
            this.messages.put(tickToDeliver, new ArrayList<Message>());
        }
        this.messages.get(tickToDeliver).add(message);
    }
    
    @Override
    public ArrayList<Message> getMessages(final Constants.GHOST querier) {
        final ArrayList<Message> results = new ArrayList<Message>();
        if (!this.messages.containsKey(this.currentTick)) {
            return results;
        }
        for (final Message message : this.messages.get(this.currentTick)) {
            if (!message.getSender().equals(querier)) {
                if (message.getRecipient() == null) {
                    results.add(message);
                }
                else {
                    if (!message.getRecipient().equals(querier)) {
                        continue;
                    }
                    results.add(message);
                }
            }
        }
        return results;
    }
}
