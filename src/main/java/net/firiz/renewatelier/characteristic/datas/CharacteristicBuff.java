package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.buff.BuffType;
import org.jetbrains.annotations.Nullable;

public class CharacteristicBuff implements CharacteristicData {

    // バフ・デバフ <BuffType, 確率, 時間, 値>
    private final BuffType buffType;
    private final int percent;
    private final int duration;
    private final int x;

    // バフ・デバフ <BuffType, 確率, 時間, 値, オブジェクト>
    @Nullable
    private final String y;

    public CharacteristicBuff(BuffType buffType, int percent, int duration, int x) {
        this.buffType = buffType;
        this.percent = percent;
        this.duration = duration;
        this.x = x;
        this.y = null;
    }

    public CharacteristicBuff(BuffType buffType, int percent, int duration, int x, @Nullable String y) {
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
