package net.firiz.renewatelier.buff;

import net.firiz.renewatelier.loop.LoopManager;

public class Buff {

    private final BuffType type;
    private final int limitDuration;
    private final int x;

    private static final LoopManager loopManager = LoopManager.INSTANCE;
    private int duration = 0;

    public Buff(BuffType type, int duration, int limitDuration, int x) {
        this.type = type;
        this.limitDuration = limitDuration;
        this.x = x;
    }

    public BuffType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public void startTimer() {
        loopManager.addLoopEffect(this::timer);
    }

    private void timer() {
        if (incrementTimer()) {
            loopManager.removeLoopEffect(this::timer);
        }
    }

    private boolean incrementTimer() {
        duration++;
        System.out.println(duration + " / " + limitDuration);
        return limitDuration <= duration;
    }
}
