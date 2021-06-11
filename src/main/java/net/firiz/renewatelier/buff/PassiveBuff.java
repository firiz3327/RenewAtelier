package net.firiz.renewatelier.buff;

import net.firiz.renewatelier.entity.EntityStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PassiveBuff implements IBuff {

    private final BuffData buffData;
    private EntityStatus status;

    public PassiveBuff(EntityStatus status, BuffData buffData) {
        this.status = status;
        this.buffData = buffData;
    }

    public void setStatus(@NotNull EntityStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public BuffValueType getBuffValueType() {
        return buffData.buffValueType;
    }

    public int getLevel() {
        return buffData.level;
    }

    public BuffType getType() {
        return buffData.type;
    }

    public int getX() {
        return buffData.x;
    }

    @Nullable
    public String getY() {
        return buffData.y;
    }
}
