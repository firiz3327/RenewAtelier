package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.data.BombData;
import net.firiz.renewatelier.skill.data.ItemSkillData;
import net.firiz.renewatelier.skill.effect.BombEffect;
import org.bukkit.entity.Player;

import java.util.function.Function;

public enum EnumItemSkill {
    BOMB1(
            true,
            new BombData(new BombEffect(false), 5, 50, 3, AttackAttribute.FIRE),
            (param) -> new BombSkill((BombData) param.data, param.player, param.itemStatus, param.force)
    );

    private final boolean isForce;
    private final ItemSkillData data;
    private final Function<Param, ItemSkill<?>> skillDataSupplier;

    EnumItemSkill(boolean isForce, ItemSkillData data, Function<Param, ItemSkill<?>> skillSupplier) {
        this.isForce = isForce;
        this.data = data;
        this.skillDataSupplier = skillSupplier;
    }

    public boolean isForce() {
        return isForce;
    }

    public ItemSkill<?> createSkill(Player player, AlchemyItemStatus itemStatus) {
        if (isForce) {
            throw new IllegalStateException("force skill.");
        }
        return skillDataSupplier.apply(new Param(data, player, itemStatus));
    }

    public ForceItemSkill<?> createSkill(Player player, AlchemyItemStatus itemStatus, float force) {
        if (!isForce) {
            throw new IllegalStateException("non force skill.");
        }
        return (ForceItemSkill<?>) skillDataSupplier.apply(new Param(data, player, itemStatus, force));
    }

    public ItemSkillData getData() {
        return data;
    }

    private static final class Param {
        final ItemSkillData data;
        final Player player;
        final AlchemyItemStatus itemStatus;
        final float force;

        public Param(ItemSkillData data, Player player, AlchemyItemStatus itemStatus) {
            this.data = data;
            this.player = player;
            this.itemStatus = itemStatus;
            this.force = 0;
        }

        public Param(ItemSkillData data, Player player, AlchemyItemStatus itemStatus, float force) {
            this.data = data;
            this.player = player;
            this.itemStatus = itemStatus;
            this.force = force;
        }
    }

}
