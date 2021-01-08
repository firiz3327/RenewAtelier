package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import org.jetbrains.annotations.Nullable;

public class ChBuff implements ChData {

    private final BuffData buffData;
    private final int percent;

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
        this.buffData = new BuffData(BuffValueType.CHARACTERISTIC, x, buffType, duration, x, y);
        this.percent = percent;
    }

    public BuffData getBuffData() {
        return buffData;
    }

    public int getPercent() {
        return percent;
    }

    public int getX() {
        return buffData.getX();
    }
}
