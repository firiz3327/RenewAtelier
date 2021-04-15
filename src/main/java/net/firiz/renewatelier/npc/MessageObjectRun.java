package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.loop.TickRunnable;

import java.util.List;

public class MessageObjectRun implements TickRunnable {

    private int time = 0;
    private final List<MessageObject> messages = new ObjectArrayList<>();

    @Override
    public void run() {
        if (messages.isEmpty()) {
            time = 0;
        } else {
            if (time > 60) {
                messages.forEach(MessageObject::hideStand);
                messages.clear();
            }
            time++;
        }
    }

    public void add(MessageObject messageObject) {
        messages.add(messageObject);
        time = 0;
    }

    public void resetTime() {
        time = 0;
    }

}
