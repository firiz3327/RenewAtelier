package net.firiz.renewatelier.entity.arrow;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.skills.character.PlayerSkillManager;
import net.firiz.renewatelier.skills.character.skill.EnumPlayerSkill;
import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierTippedArrow;
import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierSpectralArrow;
import net.firiz.renewatelier.version.entity.projectile.arrow.INMSAtelierArrow;
import net.minecraft.world.entity.projectile.EntityArrow;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public enum ArrowManager {
    INSTANCE;

    private static final String STR_NO_AVAILABLE_ARROW = "使用可能な矢がありません。";

    private enum CustomArrow {
        BAG(1);

        private final int customModel;

        CustomArrow(int customModel) {
            this.customModel = customModel;
        }

        /**
         * @param item 検索するアイテム
         * @return CustomArrowに該当する若しくは、光の矢でカスタムモデルデータを所持している場合 true
         */
        static boolean isFound(ItemStack item) {
            final ItemMeta meta = item.getItemMeta();
            return (item.getType() == Material.SPECTRAL_ARROW && meta.hasCustomModelData()) || (meta.hasCustomModelData() && Arrays.stream(values()).anyMatch(arrow -> arrow.customModel == meta.getCustomModelData()));
        }
    }

    public boolean interactCrossbow(@NotNull Player player) {
        return interactCrossbow2(player).result;
    }

    public void interactBow(@NotNull Player player) {
        final ItemStack bow = player.getInventory().getItemInMainHand();
        if (EnumPlayerSkill.Weapon.STAFF.similarItem(bow) || (player.isSneaking() && EnumPlayerSkill.Weapon.BOW.similarItem(bow))) {
            final CharStats stats = PlayerSaveManager.INSTANCE.getChar(player).getCharStats();
            stats.getSkillManager().fireSkill(stats.getWeapon());
        }
    }

    private InteractCrossbowResult interactCrossbow2(@NotNull Player player) {
        final ObjectObjectImmutablePair<ItemStack, ItemStack> arrows = getConsumeArrow(player);
        final ItemStack consumeArrow = arrows.left();
        if (consumeArrow != null) {
            final ItemStack nextConsumeArrow = arrows.right();
            if (CustomArrow.isFound(consumeArrow) && nextConsumeArrow == null) {
                player.sendMessage(STR_NO_AVAILABLE_ARROW);
            } else {
                return new InteractCrossbowResult(arrows, false);
            }
        }
        return new InteractCrossbowResult(arrows, true);
    }

    public void shootCrossbow(@NotNull Player player, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow) {
        removeArrow(baseArrow);
        shootAtelierArrow(player, bow, baseArrow, consumeArrow, false, 1, true, false);
    }

    public boolean shootBow(@NotNull LivingEntity entity, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, float force) {
        if (bow == null) {
            baseArrow.remove();
            return true;
        }

        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
        if (entity instanceof Player) {
            final CharStats stats = PlayerSaveManager.INSTANCE.getChar(entity).getCharStats();
            final PlayerSkillManager skillManager = stats.getSkillManager();
            if (EnumPlayerSkill.Weapon.STAFF.similarItem(bow) || (skillManager.getNowSkill() != null && EnumPlayerSkill.Weapon.BOW.similarItem(bow))) {
                skillManager.shoot(baseArrow);
                return true;
            }
            final ObjectObjectImmutablePair<ItemStack, ItemStack> arrows = getConsumeArrow((Player) entity);
            consumeArrow = arrows.left();
            nextConsumeArrow = arrows.right();
        }

        if (consumeArrow != null) { // always entity is player
            final boolean result;
            if (CustomArrow.isFound(consumeArrow)) {
                removeArrow(baseArrow);
                result = shootNextArrow((Player) entity, bow, baseArrow, consumeArrow, nextConsumeArrow, force);
            } else {
                removeArrow(baseArrow);
                shootAtelierArrow(entity, bow, baseArrow, consumeArrow, true, force, false);
                result = false;
            }
            ((Player) entity).updateInventory();
            return result;
        } else if (!(entity instanceof Player) || ((Player) entity).getGameMode() == GameMode.CREATIVE) {
            removeArrow(baseArrow);
            shootAtelierArrow(entity, bow, baseArrow, new ItemStack(Material.ARROW), true, force, false);
        }
        return false;
    }

    private void removeArrow(final AbstractArrow arrow) {
        arrow.remove();
    }

    private void soundArrow(final LivingEntity source) {
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
    }

    public AtelierAbstractArrow shootSkillArrow(@NotNull LivingEntity shooter, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final float force, final boolean isCrossbow) {
        return shootAtelierArrow(shooter, bow, baseArrow, consumeArrow, false, force, isCrossbow, true).getAtelierArrowEntity();
    }

    private void shootAtelierArrow(@NotNull LivingEntity shooter, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final boolean isConsumeArrow, final float force, final boolean isSkill) {
        shootAtelierArrow(shooter, bow, baseArrow, consumeArrow, isConsumeArrow, force, false, isSkill);
    }

    private INMSAtelierArrow shootAtelierArrow(@NotNull LivingEntity shooter, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final boolean isConsumeArrow, final float force, final boolean isCrossbow, final boolean isSkill) {
        final ItemStack oneArrow = consumeArrow.clone();
        oneArrow.setAmount(1);

        INMSAtelierArrow nmsCloneArrow;
        switch (consumeArrow.getType()) {
            case ARROW -> nmsCloneArrow = new NMSAtelierTippedArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
            case TIPPED_ARROW -> {
                nmsCloneArrow = new NMSAtelierTippedArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
                final PotionMeta potionMeta = (PotionMeta) consumeArrow.getItemMeta();
                assert potionMeta != null;
                final NMSAtelierTippedArrow nmsArrow = (NMSAtelierTippedArrow) nmsCloneArrow;
                final AtelierTippedArrow arrow = nmsArrow.getAtelierArrowEntity();
                arrow.setBasePotionData(potionMeta.getBasePotionData());
                potionMeta.getCustomEffects().forEach(potionEffect -> arrow.addCustomEffect(potionEffect, true));
                if (potionMeta.hasColor() && potionMeta.getColor() != null) {
                    arrow.setColor(potionMeta.getColor());
                }
            }
            case SPECTRAL_ARROW -> nmsCloneArrow = new NMSAtelierSpectralArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
            default -> throw new IllegalStateException("consumeArrow is not arrow.");
        }
        final AtelierAbstractArrow cloneArrow = nmsCloneArrow.getAtelierArrowEntity();
        cloneArrow.setShooter(baseArrow.getShooter());
        cloneArrow.setFireTicks(baseArrow.getFireTicks());
        cloneArrow.setPierceLevel(baseArrow.getPierceLevel());
        cloneArrow.setKnockbackStrength(baseArrow.getKnockbackStrength());
        cloneArrow.setDamage(baseArrow.getDamage());
        cloneArrow.setCritical(baseArrow.isCritical());
        ((EntityArrow) nmsCloneArrow).setShotFromCrossbow(isCrossbow);

        if (!(shooter instanceof Player) || ((Player) shooter).getGameMode() == GameMode.CREATIVE) {
            cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        } else if (!Objects.requireNonNull(bow.getItemMeta()).hasEnchant(Enchantment.ARROW_INFINITE)) {
            if (isConsumeArrow) {
                consumeArrow.setAmount(consumeArrow.getAmount() - 1);
            }
            cloneArrow.setPickupStatus(baseArrow.getPickupStatus());
        } else {
            cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
        soundArrow(shooter);
        nmsCloneArrow.shoot(baseArrow.getVelocity());
        return nmsCloneArrow;
    }

    private boolean shootNextArrow(@NotNull Player player, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, @Nullable ItemStack nextConsumeArrow, float force) {
        if (nextConsumeArrow == null) {
            player.sendMessage(STR_NO_AVAILABLE_ARROW);
            final int consumeArrowAmount = consumeArrow.getAmount();
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> consumeArrow.setAmount(consumeArrowAmount),
                    1L
            );
            return true;
        }
        shootAtelierArrow(player, bow == null ? new ItemStack(Material.BOW) : bow, baseArrow, nextConsumeArrow, true, force, false);
        final int consumeArrowAmount = consumeArrow.getAmount();
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> consumeArrow.setAmount(consumeArrowAmount),
                1L
        );
        return false;
    }

    private ObjectObjectImmutablePair<ItemStack, ItemStack> getConsumeArrow(@NotNull Player player) {
        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
        final PlayerInventory playerInventory = player.getInventory();
        final List<ItemStack> items = new ObjectArrayList<>();
        items.add(playerInventory.getItemInMainHand());
        items.add(playerInventory.getItemInOffHand());
        IntStream.rangeClosed(0, 35).mapToObj(playerInventory::getItem).forEach(items::add);
        for (final ItemStack item : items) {
            if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.TIPPED_ARROW || item.getType() == Material.SPECTRAL_ARROW)) {
                if (consumeArrow == null) {
                    consumeArrow = item;
                } else if (!CustomArrow.isFound(item)) {
                    nextConsumeArrow = item;
                    break;
                }
            }
        }
        return new ObjectObjectImmutablePair<>(consumeArrow, nextConsumeArrow);
    }

    private record InteractCrossbowResult(
            ObjectObjectImmutablePair<ItemStack, ItemStack> arrows,
            boolean result
    ) {

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (InteractCrossbowResult) obj;
            return Objects.equals(this.arrows, that.arrows) &&
                    this.result == that.result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(arrows, result);
        }

        @Override
        public String toString() {
            return "InteractCrossbowResult[" +
                    "arrows=" + arrows + ", " +
                    "result=" + result + ']';
        }

    }

}
