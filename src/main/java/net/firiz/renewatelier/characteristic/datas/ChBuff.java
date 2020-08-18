package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.buff.BuffType;
import org.jetbrains.annotations.Nullable;

public class ChBuff implements ChData {

    // バフ・デバフ <BuffType, 確率, 時間, 値>
    private final BuffType buffType;
    private final int percent;
    private final int duration;
    private final int x;

    // バフ・デバフ <BuffType, 確率, 時間, 値, オブジェクト>
    @Nullable
    private final String y;

    public static ChData newInstance(String[] args) {
        if (args.length == 4) {
            return new ChBuff(BuffType.valueOf(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        } else {
            return new ChBuff(BuffType.valueOf(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4]);
        }
    }

    private ChBuff(BuffType buffType, int percent, int duration, int x) {
        this(buffType, percent, duration, x, null);
    }

    private ChBuff(BuffType buffType, int percent, int duration, int x, @Nullable String y) {
        this.buffType = buffType;
        this.percent = percent;
        this.duration = duration;
        this.x = x;
        this.y = y;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    public int getPercent() {
        return percent;
    }

    public int getDuration() {
        return duration;
    }

    public int getX() {
        return x;
    }

    @Nullable
    public String getY() {
        return y;
    }
}
