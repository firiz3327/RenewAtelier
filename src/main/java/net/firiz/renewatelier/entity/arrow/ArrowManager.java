package net.firiz.renewatelier.entity.arrow;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.version.entity.projectile.arrow.NMSAtelierArrow;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class ArrowManager {

    private enum CustomArrow {
        BOOK(1);

        private final int customModel;

        CustomArrow(int customModel) {
            this.customModel = customModel;
        }

        static boolean isFound(ItemMeta meta) {
            return meta.hasCustomModelData() && Arrays.stream(values()).anyMatch(arrow -> arrow.customModel == meta.getCustomModelData());
        }
    }

    public void shootCrossbow(@NotNull Player player, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow) {
        cancelArrow(player, baseArrow);
        shootAtelierArrow(player, bow, baseArrow, consumeArrow, false, 1);
    }

    public void shootBow(@NotNull LivingEntity entity, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, float force) {
        if (bow == null) {
            baseArrow.remove();
            return;
        }

        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
        if (entity instanceof Player) {
            final PlayerInventory playerInventory = ((Player) entity).getInventory();
            final List<ItemStack> items = new ArrayList<>();
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
        }

        if (consumeArrow != null) { // always entity is player
            final ItemMeta meta = Objects.requireNonNull(consumeArrow.getItemMeta());
            if (CustomArrow.isFound(meta)) {
                cancelArrow(entity, baseArrow);
                shootNextArrow((Player) entity, bow, baseArrow, consumeArrow, nextConsumeArrow, force);
            } else {
                cancelArrow(entity, baseArrow);
                shootAtelierArrow(entity, bow, baseArrow, consumeArrow, true, force);
            }
        } else if (!(entity instanceof Player) || ((Player) entity).getGameMode() == GameMode.CREATIVE) {
            cancelArrow(entity, baseArrow);
            shootAtelierArrow(entity, bow, baseArrow, new ItemStack(Material.ARROW), true, force);
        }
    }

    private void cancelArrow(final LivingEntity source, final AbstractArrow arrow) {
        arrow.remove();
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
    }

    private void shootAtelierArrow(@NotNull LivingEntity entity, @NotNull ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, final boolean isConsumeArrow, final float force) {
        final ItemStack oneArrow = consumeArrow.clone();
        oneArrow.setAmount(1);

        IAtelierArrow cloneArrow;
        switch (consumeArrow.getType()) {
            case ARROW:
                cloneArrow = new NMSAtelierArrow(entity.getEyeLocation(), bow, oneArrow, entity, force);
                break;
            case TIPPED_ARROW:
                cloneArrow = new NMSAtelierArrow(entity.getEyeLocation(), bow, oneArrow, entity, force);
                final PotionMeta potionMeta = (PotionMeta) consumeArrow.getItemMeta();
                assert potionMeta != null;

                final NMSAtelierArrow arrow = (NMSAtelierArrow) cloneArrow;
                arrow.setBasePotionData(potionMeta.getBasePotionData());
                potionMeta.getCustomEffects().forEach(potionEffect -> arrow.addCustomEffect(potionEffect, true));
                if (potionMeta.hasColor() && potionMeta.getColor() != null) {
                    arrow.setColor(potionMeta.getColor());
                }
                break;
            case SPECTRAL_ARROW:
                cloneArrow = new NMSAtelierSpectralArrow(entity.getEyeLocation(), bow, oneArrow, entity, force);
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

        if (!(entity instanceof Player) || ((Player) entity).getGameMode() == GameMode.CREATIVE) {
            cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        } else if (!Objects.requireNonNull(bow.getItemMeta()).hasEnchant(Enchantment.ARROW_INFINITE)) {
            if (isConsumeArrow) {
                consumeArrow.setAmount(consumeArrow.getAmount() - 1);
            }
            cloneArrow.setPickupStatus(baseArrow.getPickupStatus());
        } else {
            cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
        cloneArrow.shoot(baseArrow.getVelocity());
    }

    private void shootNextArrow(@NotNull Player player, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, @Nullable ItemStack nextConsumeArrow, float force) {
        if (nextConsumeArrow == null) {
            player.sendMessage("使用可能な矢がありません。");
            final int consumeArrowAmount = consumeArrow.getAmount();
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> consumeArrow.setAmount(consumeArrowAmount),
                    1L
            );
            return;
        }
        shootAtelierArrow(player, bow == null ? new ItemStack(Material.BOW) : bow, baseArrow, nextConsumeArrow, true, force);
        final int consumeArrowAmount = consumeArrow.getAmount();
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> consumeArrow.setAmount(consumeArrowAmount),
                1L
        );
    }

}
