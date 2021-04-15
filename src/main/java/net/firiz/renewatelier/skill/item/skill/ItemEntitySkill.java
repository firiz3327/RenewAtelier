package net.firiz.renewatelier.skill.item.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.item.data.ItemSkillData;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemEntitySkill<T extends ItemSkillData> extends ItemSkill<T> {

    @Nullable
    protected final Entity entity;

    public ItemEntitySkill(@NotNull T data, @NotNull Char character, @NotNull AlchemyItemStatus itemStatus, @Nullable Entity entity) {
        super(data, character, itemStatus);
        this.entity = entity;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }
}
