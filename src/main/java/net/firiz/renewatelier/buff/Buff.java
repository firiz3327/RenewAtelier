package net.firiz.renewatelier.buff;

import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.utils.Chore;

public class Buff {

    private final String value;
    private final BuffValueType buffValueType;
    private final int level;
    private final BuffType type;
    private final int limitDuration;
    private final int x;
    private Runnable timer;

    private static final LoopManager loopManager = LoopManager.INSTANCE;
    private int duration;

    private boolean end;
    private Runnable endHandler;

    public Buff(String value, BuffValueType buffValueType, int level, BuffType type, int duration, int limitDuration, int x) {
        this.value = value;
        this.buffValueType = buffValueType;
        this.level = level;
        this.type = type;
        this.duration = duration;
        this.limitDuration = limitDuration;
        this.x = x;
        this.timer = () -> {
            if (incrementTimer()) {
                stopTimer();
            }
        };
    }

    public String getValue() {
        return value;
    }

    public BuffValueType getBuffValueType() {
        return buffValueType;
    }

    public int getLevel() {
        return level;
    }

    public BuffType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEndHandler(Runnable handler) {
        this.endHandler = handler;
    }

    public void startTimer() {
        loopManager.addSec(timer);
    }

    private boolean incrementTimer() {
        duration++;
        Chore.log(duration + " / " + limitDuration);
        return limitDuration <= duration;
    }

    public void stopTimer() {
        loopManager.removeSec(timer);
        end = true;
        endHandler.run();
    }
}
