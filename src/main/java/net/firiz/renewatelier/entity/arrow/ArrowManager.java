package net.firiz.renewatelier.entity.arrow;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.chores.CObjects;
import net.firiz.renewatelier.utils.pair.ImmutableNullablePair;
import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierTippedArrow;
import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierSpectralArrow;
import net.firiz.renewatelier.version.entity.projectile.arrow.IAtelierArrow;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
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

        static boolean isFound(ItemMeta meta) {
            return meta.hasCustomModelData() && Arrays.stream(values()).anyMatch(arrow -> arrow.customModel == meta.getCustomModelData());
        }
    }

    public boolean handleDigPacketCrossbow(@NotNull Player player) {
        final ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.CROSSBOW) {
            final InteractCrossbowResult data = ArrowManager.INSTANCE.interactCrossbow2(player);
            if (data.result) { // consumeArrow and nextConsumeArrow is null
                player.updateInventory();
                return true;
            } else if (CObjects.nullIfPredicate(
                    data.arrows.getLeft(),
                    itemStack -> CustomArrow.isFound(itemStack.getItemMeta()),
                    false)) {
                final ItemStack nextConsumeArrow = data.arrows.getRight();
                if (nextConsumeArrow != null) {
                    final ItemStack arrow = nextConsumeArrow.clone();
                    arrow.setAmount(1);
                    final CrossbowMeta meta = (CrossbowMeta) mainHand.getItemMeta();
                    meta.addChargedProjectile(arrow);
                    mainHand.setItemMeta(meta);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        nextConsumeArrow.setAmount(nextConsumeArrow.getAmount() - 1);
                    }
                }
                player.updateInventory();
                return true;
            }
        }
        return false;
    }

    public boolean interactCrossbow(@NotNull Player player) {
        return interactCrossbow2(player).result;
    }

    private InteractCrossbowResult interactCrossbow2(@NotNull Player player) {
        final ImmutableNullablePair<ItemStack, ItemStack> arrows = getConsumeArrow(player);
        final ItemStack consumeArrow = arrows.getLeft();
        if (consumeArrow != null) {
            final ItemStack nextConsumeArrow = arrows.getRight();
            if (CustomArrow.isFound(consumeArrow.getItemMeta()) && nextConsumeArrow == null) {
                player.sendMessage(STR_NO_AVAILABLE_ARROW);
            } else {
                return new InteractCrossbowResult(arrows, false);
            }
        }
        return new InteractCrossbowResult(arrows, true);
    }

    public void shootCrossbow(@NotNull Player player, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow) {
        removeArrow(baseArrow);
        shootAtelierArrow(player, bow, baseArrow, consumeArrow, false, 1, false);
    }

    public boolean shootBow(@NotNull LivingEntity entity, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, float force) {
        if (bow == null) {
            baseArrow.remove();
            return true;
        }

        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
        if (entity instanceof Player) {
            final ImmutableNullablePair<ItemStack, ItemStack> arrows = getConsumeArrow((Player) entity);
            consumeArrow = arrows.getLeft();
            nextConsumeArrow = arrows.getRight();
        }

        if (consumeArrow != null) { // always entity is player
            final ItemMeta meta = Objects.requireNonNull(consumeArrow.getItemMeta());
            if (CustomArrow.isFound(meta)) {
                removeArrow(baseArrow);
                return shootNextArrow((Player) entity, bow, baseArrow, consumeArrow, nextConsumeArrow, force);
            } else {
                removeArrow(baseArrow);
                shootAtelierArrow(entity, bow, baseArrow, consumeArrow, true, force, false);
            }
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

    public AtelierAbstractArrow shootSkillArrow(@NotNull LivingEntity shooter, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final float force) {
        return shootAtelierArrow(shooter, bow, baseArrow, consumeArrow, false, force, true).getAtelierArrowEntity();
    }

    private IAtelierArrow shootAtelierArrow(@NotNull LivingEntity shooter, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final boolean isConsumeArrow, final float force, final boolean isSkill) {
        final ItemStack oneArrow = consumeArrow.clone();
        oneArrow.setAmount(1);

        IAtelierArrow cloneArrow;
        switch (consumeArrow.getType()) {
            case ARROW:
                cloneArrow = new NMSAtelierTippedArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
                break;
            case TIPPED_ARROW:
                cloneArrow = new NMSAtelierTippedArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
                final PotionMeta potionMeta = (PotionMeta) consumeArrow.getItemMeta();
                assert potionMeta != null;

                final NMSAtelierTippedArrow arrow = (NMSAtelierTippedArrow) cloneArrow;
                arrow.setBasePotionData(potionMeta.getBasePotionData());
                potionMeta.getCustomEffects().forEach(potionEffect -> arrow.addCustomEffect(potionEffect, true));
                if (potionMeta.hasColor() && potionMeta.getColor() != null) {
                    arrow.setColor(potionMeta.getColor());
                }
                break;
            case SPECTRAL_ARROW:
                cloneArrow = new NMSAtelierSpectralArrow(shooter.getEyeLocation(), bow, oneArrow, shooter, force, isSkill);
                break;
            default:
                throw new IllegalStateException("consumeArrow is not arrow.");
        }
        cloneArrow.setShooter(baseArrow.getShooter());
        cloneArrow.setFireTicks(baseArrow.getFireTicks());
        cloneArrow.setPierceLevel(baseArrow.getPierceLevel());
        cloneArrow.setKnockbackStrength(baseArrow.getKnockbackStrength());
        cloneArrow.setDamage(baseArrow.getDamage());
        cloneArrow.setCritical(baseArrow.isCritical());

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
        cloneArrow.shoot(baseArrow.getVelocity());
        return cloneArrow;
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

    private ImmutableNullablePair<ItemStack, ItemStack> getConsumeArrow(@NotNull Player player) {
        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
        final PlayerInventory playerInventory = player.getInventory();
        final List<ItemStack> items = new ObjectArrayList<>();
        items.add(playerInventory.getItemInMainHand());
        items.add(playerInventory.getItemInOffHand());
        IntStream.rangeClosed(0, 35).mapToObj(playerInventory::getItem).forEach(items::add);
        for (final ItemStack item : items) {
            if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.TIPPED_ARROW || item.getType() == Material.SPECTRAL_ARROW)) {
                final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                if (consumeArrow == null) {
                    consumeArrow = item;
                } else if (!CustomArrow.isFound(meta)) {
                    nextConsumeArrow = item;
                    break;
                }
            }
        }
        return new ImmutableNullablePair<>(consumeArrow, nextConsumeArrow);
    }

    private static class InteractCrossbowResult {
        final ImmutableNullablePair<ItemStack, ItemStack> arrows;
        final boolean result;

        public InteractCrossbowResult(ImmutableNullablePair<ItemStack, ItemStack> arrows, boolean result) {
            this.arrows = arrows;
            this.result = result;
        }
    }

}
