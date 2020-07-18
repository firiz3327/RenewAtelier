package net.firiz.renewatelier.version.entity.atelier.holo;

import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractHoloHealth {

    @NotNull
    protected final LivingEntity entity;
    @NotNull
    protected final String customName;
    @Nullable
    protected final LivingData livingData;

    protected AbstractHoloHealth(@NotNull LivingEntity entity, @Nullable LivingData livingData, @NotNull String customName) {
        this.entity = Objects.requireNonNull(entity);
        this.customName = Objects.requireNonNull(customName);
        this.livingData = livingData;
    }

    public abstract void holo();

    public abstract void die();

    protected double getPercentHealth() {
        return entity.getHealth() / Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
    }
}
