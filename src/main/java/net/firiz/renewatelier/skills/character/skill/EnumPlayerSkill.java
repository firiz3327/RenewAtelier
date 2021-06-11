package net.firiz.renewatelier.skills.character.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.character.IPlayerSkillBuilder;
import net.firiz.renewatelier.skills.character.skill.bow.StoneShootSkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public enum EnumPlayerSkill implements IPlayerSkillBuilder {
    STONE_SHOOT(StoneShootSkill::create, 12, Weapon.STAFF);

    private final BiFunction<Char, AlchemyItemStatus, CharSkill> createSkillSupp;
    private final int mp;
    private final Weapon weaponType;

    EnumPlayerSkill(BiFunction<Char, AlchemyItemStatus, CharSkill> createSkillSupp, int mp, Weapon weaponType) {
        this.createSkillSupp = createSkillSupp;
        this.mp = mp;
        this.weaponType = weaponType;
    }

    public enum Weapon {
        BOW(Material.BOW),
        STAFF(Material.BOW, 1);

        private final Material material;
        private final int customModelData;
        private final boolean hasCustomModelData;

        Weapon(Material material) {
            this(material, 0, false);
        }

        Weapon(Material material, int customModelData) {
            this(material, customModelData, true);
        }

        Weapon(Material material, int customModelData, boolean hasCustomModelData) {
            this.material = material;
            this.customModelData = customModelData;
            this.hasCustomModelData = hasCustomModelData;
        }

        @Nullable
        public static Weapon searchWeapon(@Nullable final ItemStack itemStack) {
            if (itemStack != null) {
                for (final Weapon weapon : values()) {
                    if (weapon.similarItem(itemStack)) {
                        return weapon;
                    }
                }
            }
            return null;
        }

        @Contract("null -> false")
        public boolean similarItem(@Nullable final ItemStack itemStack) {
            if (itemStack != null && material == itemStack.getType()) {
                if (hasCustomModelData) {
                    final ItemMeta meta = itemStack.getItemMeta();
                    return meta.hasCustomModelData() && customModelData == meta.getCustomModelData();
                }
                return true;
            }
            return false;
        }

    }

    public boolean consumeMp(Char character) {
        final CharStats stats = character.getCharStats();
        if (stats.getMp() >= mp) {
            stats.damageMp(mp);
            return true;
        }
        return false;
    }

    public CharSkill createSkill(Player player, AlchemyItemStatus itemStatus) {
        return createSkillSupp.apply(PlayerSaveManager.INSTANCE.getChar(player), itemStatus);
    }

    public CharSkill createSkill(Char player, AlchemyItemStatus itemStatus) {
        return createSkillSupp.apply(player, itemStatus);
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public boolean isPassive() {
        return false;
    }

}
