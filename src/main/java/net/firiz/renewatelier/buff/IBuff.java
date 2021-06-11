package net.firiz.renewatelier.buff;

import net.firiz.renewatelier.entity.EntityStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBuff {

    void setStatus(@NotNull EntityStatus status);

    BuffValueType getBuffValueType();

    int getLevel();

    BuffType getType();

    int getX();

    @Nullable
    String getY();
}
