package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.effect.RangeEffect;
import net.firiz.renewatelier.skill.item.data.BombData;
import net.firiz.renewatelier.skill.item.data.ItemSkillData;
import net.firiz.renewatelier.skill.effect.BombEffect;
import net.firiz.renewatelier.skill.item.data.RangeData;
import net.firiz.renewatelier.skill.item.skill.BombSkill;
import net.firiz.renewatelier.skill.item.skill.ItemSkill;
import net.firiz.renewatelier.skill.item.skill.RangeSkill;
import net.firiz.renewatelier.utils.ParticleData;
import net.firiz.renewatelier.utils.chores.CObjects;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum EnumItemSkill {
    BOMB1(
            new BombData(new BombEffect(false), 3, AttackAttribute.FIRE),
            param -> new BombSkill((BombData) param.data, param.character, param.itemStatus)
    ),
    RANGE_HEAL1(
            new RangeData(new RangeEffect(new ParticleData(Particle.VILLAGER_HAPPY, 50, 6, 3, 6)), 6, 3, 4),
            param -> new RangeSkill((RangeData) param.data, param.character, param.itemStatus)
    );

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final ItemSkillData data;
    private final Function<Param, ItemSkill<?>> skillDataSupplier;

    EnumItemSkill(ItemSkillData data, Function<Param, ItemSkill<?>> skillSupplier) {
        this.data = data;
        this.skillDataSupplier = skillSupplier;
    }

    public ItemSkill<?> createSkill(Player player, AlchemyItemStatus itemStatus) {
        final Char character = psm.getChar(player.getUniqueId());
        cooldown(character, itemStatus);
        return skillDataSupplier.apply(new Param(data, character, itemStatus));
    }

    public int getCooldown(AlchemyItemStatus itemStatus) {
        return getCooldown(null, itemStatus);
    }

    public int getCooldown(@Nullable Char character, AlchemyItemStatus itemStatus) {
        final int percentReduce = itemStatus.getCharacteristics().stream().filter(c -> c.hasData(CharacteristicType.USE_SPEED)).mapToInt(c -> c.getIntData(CharacteristicType.USE_SPEED)).sum();
        long coolTimeMillis = (long) (itemStatus.getAlchemyMaterial().getItemCooldown() * 1000) - CObjects.nullIfFunction(character, c -> c.getCharStats().getSpeed(), 0);
        coolTimeMillis *= (100D + percentReduce) / 100D; // percentReduceが 正の値だと遅くなり、負の値だと早くなる
        return Math.max(5, (int) (coolTimeMillis * 0.02)); // 素手より早くなることはない
    }

    private void cooldown(Char character, AlchemyItemStatus itemStatus) {
        character.getPlayer().setCooldown(GameConstants.USABLE_MATERIAL, getCooldown(character, itemStatus));
    }

    public ItemSkillData getData() {
        return data;
    }

    private static final class Param {
        final ItemSkillData data;
        final Char character;
        final AlchemyItemStatus itemStatus;

        public Param(ItemSkillData data, Char character, AlchemyItemStatus itemStatus) {
            this.data = data;
            this.character = character;
            this.itemStatus = itemStatus;
        }
    }

}
