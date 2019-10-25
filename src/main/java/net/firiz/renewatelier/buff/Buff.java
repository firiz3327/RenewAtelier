package net.firiz.renewatelier.buff;

public class Buff {

    private final BuffType type;
    private final int duration;
    private final int x;

    public Buff(BuffType type, int duration, int x) {
        this.type = type;
        this.duration = duration;
        this.x = x;
    }

    public BuffType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getX() {
        return x;
    }
}
