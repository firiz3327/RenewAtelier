package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.buff.BuffType;
import org.jetbrains.annotations.Nullable;

public class ChClearDebuff implements ChData {

    private final boolean isAll;
    @Nullable
    private final BuffType buffType;

    private ChClearDebuff(boolean isAll, @Nullable BuffType buffType) {
        this.isAll = isAll;
        this.buffType = buffType;
    }

    public static ChData newInstance(String[] args) {
        final String value = args[0];
        if (value.equalsIgnoreCase("all")) {
            return new ChClearDebuff(true, null);
        }
        return new ChClearDebuff(false, BuffType.valueOf(value));
    }

    public boolean isAll() {
        return isAll;
    }

    @Nullable
    public BuffType getBuffType() {
        return buffType;
    }
}
