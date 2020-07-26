package net.firiz.renewatelier.buff;

import org.jetbrains.annotations.Nullable;

public class BuffData {

    protected final BuffValueType buffValueType;
    protected final int level;
    protected final BuffType type;
    protected final int limitDuration;
    protected final int x;
    @Nullable
    protected final String y;

    public BuffData(BuffValueType buffValueType, int level, BuffType type, int limitDuration, int x) {
        this.buffValueType = buffValueType;
        this.level = level;
        this.type = type;
        this.limitDuration = limitDuration;
        this.x = x;
        this.y = null;
    }

    public BuffData(BuffValueType buffValueType, int level, BuffType type, int limitDuration, int x, @Nullable String y) {
        this.buffValueType = buffValueType;
        this.level = level;
        this.type = type;
        this.limitDuration = limitDuration;
        this.x = x;
        this.y = y;
    }
}
