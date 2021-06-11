package net.firiz.renewatelier.skills.item.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.item.data.InteractData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractSkill extends ItemEntitySkill<InteractData> {

    public InteractSkill(@NotNull InteractData data, @NotNull Char character, @NotNull AlchemyItemStatus itemStatus, @Nullable Entity entity) {
        super(data, character, itemStatus, entity);
    }

    @Override
    public boolean fire() {
        final LivingEntity targetEntity = type();
        if (targetEntity == null) {
            return false;
        } else {
            data.getEffect().effect(targetEntity.getLocation());
            if (data.isHeal()) {
                damageUtilV2.itemHeal(itemStatus, getPlayer(), targetEntity);
            } else {
                damageUtilV2.itemDamage(itemStatus, getPlayer(), targetEntity, 1, data.getAttackAttribute());
            }
        }
        return true;
    }

    @Nullable
    private LivingEntity type() {
        switch (data.getType()) {
            case INTERACT:
                return getPlayer();
            case INTERACT_ENTITY:
                if (this.entity != null && this.entity instanceof LivingEntity) {
                    return (LivingEntity) this.entity;
                } else {
                    return getPlayer();
                }
            case TARGET_ENTITY:
                if (this.entity == null) {
                    return null;
                }
                return (LivingEntity) this.entity;
            case INTERACT_PLAYER:
                if (this.entity != null && this.entity instanceof Player) {
                    return (LivingEntity) this.entity;
                } else {
                    return getPlayer();
                }
            case TARGET_PLAYER:
                if (this.entity == null || !(this.entity instanceof Player)) {
                    return null;
                }
                return (LivingEntity) this.entity;
            default:
                return null;
        }
    }

}
