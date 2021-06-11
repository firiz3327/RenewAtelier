package net.firiz.renewatelier.skills.item.skill;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.inventory.item.json.itemeffect.AlchemyItemEffect;
import net.firiz.renewatelier.skills.item.data.BombData;
import net.firiz.renewatelier.utils.GArray;
import net.firiz.renewatelier.utils.minecraft.BlockUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.item.IBombProjectile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BlastingBombSkill extends BombSkill {

    private static final ItemStack[] breakTools;
    private static final GArray<Set<Material>> availableBrokenOres;

    static {
        breakTools = new ItemStack[]{
                ItemUtils.enchant(new ItemStack(Material.IRON_PICKAXE), Enchantment.LOOT_BONUS_BLOCKS, 1, true),
                ItemUtils.enchant(new ItemStack(Material.IRON_PICKAXE), Enchantment.LOOT_BONUS_BLOCKS, 2, true),
                ItemUtils.enchant(new ItemStack(Material.IRON_PICKAXE), Enchantment.LOOT_BONUS_BLOCKS, 3, true),
                ItemUtils.enchant(new ItemStack(Material.IRON_PICKAXE), Enchantment.LOOT_BONUS_BLOCKS, 4, true)
        };
        availableBrokenOres = new GArray<>(3);
        availableBrokenOres.set(0, new ObjectOpenHashSet<>(new Material[]{Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE}));
        availableBrokenOres.set(1, new ObjectOpenHashSet<>(new Material[]{Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE}));
        availableBrokenOres.set(2, new ObjectOpenHashSet<>(new Material[]{Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.ANCIENT_DEBRIS}));
    }

    public BlastingBombSkill(BombData data, Char character, AlchemyItemStatus itemStatus) {
        super(data, character, itemStatus);
    }

    @Override
    protected void hit(final IBombProjectile bombProjectile, final Location location) {
        super.hit(bombProjectile, location);

        int radius;
        if (itemStatus.hasActiveEffect(AlchemyItemEffect.INCREASE_EFFECT_RANGE_1)) {
            radius = 3;
        } else if (itemStatus.hasActiveEffect(AlchemyItemEffect.INCREASE_EFFECT_RANGE_2)) {
            radius = 4;
        } else if (itemStatus.hasActiveEffect(AlchemyItemEffect.INCREASE_EFFECT_RANGE_3)) {
            radius = 5;
        } else {
            radius = 2;
        }

        int loot;
        if (itemStatus.hasActiveEffect(AlchemyItemEffect.LOOT_BONUS_1)) {
            loot = 1;
        } else if (itemStatus.hasActiveEffect(AlchemyItemEffect.LOOT_BONUS_2)) {
            loot = 2;
        } else if (itemStatus.hasActiveEffect(AlchemyItemEffect.LOOT_BONUS_3)) {
            loot = 3;
        } else {
            loot = 0;
        }

        int brokenLevel;
        if (itemStatus.hasActiveEffect(AlchemyItemEffect.BROKEN_LEVEL_2)) {
            brokenLevel = 1;
        } else if (itemStatus.hasActiveEffect(AlchemyItemEffect.BROKEN_LEVEL_3)) {
            brokenLevel = 2;
        } else {
            brokenLevel = 0;
        }

        BlockUtils.rangeBlocks(location, radius).stream()
                .filter(block -> availableBrokenOres.get(brokenLevel).contains(block.getType()))
                .forEach(block -> block.breakNaturally(breakTools[loot], true));
    }
}
