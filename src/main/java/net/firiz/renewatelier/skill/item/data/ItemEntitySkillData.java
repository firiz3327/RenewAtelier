package net.firiz.renewatelier.skill.item.data;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class ItemEntitySkillData implements ItemSkillData {

    @Nullable
    protected Entity entity;

    ItemEntitySkillData(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }
}
