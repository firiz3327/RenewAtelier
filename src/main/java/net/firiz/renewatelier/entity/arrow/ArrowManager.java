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
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
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
        final AlchemyItemStatus bowItemStatus = AlchemyItemStatus.load(bow);
        if (bowItemStatus != null) {
            cancelArrow(player, baseArrow);
            shootAtelierArrow(player, bow, baseArrow, consumeArrow, false, 1);
        }
    }

    public void shootBow(@NotNull Player player, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, float force) {
        if (bow == null) {
            baseArrow.remove();
            return;
        }
        final PlayerInventory playerInventory = player.getInventory();
        final List<ItemStack> items = new ArrayList<>();
        items.add(playerInventory.getItemInMainHand());
        items.add(playerInventory.getItemInOffHand());
        IntStream.rangeClosed(0, 35).mapToObj(playerInventory::getItem).forEach(items::add);

        ItemStack consumeArrow = null;
        ItemStack nextConsumeArrow = null;
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

        if (consumeArrow != null) {
            final AlchemyItemStatus bowItemStatus = AlchemyItemStatus.load(bow);
            final boolean hasBowItemStatus = bowItemStatus != null;

            final ItemMeta meta = Objects.requireNonNull(consumeArrow.getItemMeta());
            if (CustomArrow.isFound(meta)) {
                cancelArrow(player, baseArrow);
                shootNextArrow(hasBowItemStatus, player, bow, baseArrow, consumeArrow, nextConsumeArrow, force);
            } else if (hasBowItemStatus) {
                cancelArrow(player, baseArrow);
                shootAtelierArrow(player, bow, baseArrow, consumeArrow, true, force);
            }
        }
    }

    private void cancelArrow(final Player source, final AbstractArrow arrow) {
        arrow.remove();
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
    }

    private void shootAtelierArrow(final Player player, final ItemStack bow, final AbstractArrow baseArrow, final ItemStack consumeArrow, final boolean isConsumeArrow, final float force) {
        final ItemStack oneArrow = consumeArrow.clone();
        oneArrow.setAmount(1);

        IAtelierArrow cloneArrow;
        switch (consumeArrow.getType()) {
            case ARROW:
                cloneArrow = new NMSAtelierArrow(player.getEyeLocation(), bow, oneArrow, player, force);
                break;
            case TIPPED_ARROW:
                cloneArrow = new NMSAtelierArrow(player.getEyeLocation(), bow, oneArrow, player, force);
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
                cloneArrow = new NMSAtelierSpectralArrow(player.getEyeLocation(), bow, oneArrow, player, force);
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

        if (player.getGameMode() == GameMode.CREATIVE) {
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

    private void shootNextArrow(boolean hasItemStatus, @NotNull Player player, @Nullable ItemStack bow, @NotNull AbstractArrow baseArrow, @NotNull ItemStack consumeArrow, @Nullable ItemStack nextConsumeArrow, float force) {
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

        if (hasItemStatus && bow != null) {
            shootAtelierArrow(player, bow, baseArrow, nextConsumeArrow, true, force);
        } else {
            final AbstractArrow cloneArrow;
            switch (nextConsumeArrow.getType()) {
                case ARROW:
                    cloneArrow = player.launchProjectile(Arrow.class);
                    break;
                case TIPPED_ARROW:
                    cloneArrow = player.launchProjectile(Arrow.class);
                    final PotionMeta potionMeta = (PotionMeta) nextConsumeArrow.getItemMeta();
                    assert potionMeta != null;

                    final Arrow arrow = (Arrow) cloneArrow;
                    arrow.setBasePotionData(potionMeta.getBasePotionData());
                    potionMeta.getCustomEffects().forEach(potionEffect -> arrow.addCustomEffect(potionEffect, true));
                    if (potionMeta.hasColor() && potionMeta.getColor() != null) {
                        arrow.setColor(potionMeta.getColor());
                    }
                    break;
                case SPECTRAL_ARROW:
                    cloneArrow = player.launchProjectile(SpectralArrow.class);
                    break;
                default:
                    throw new IllegalStateException("nextConsumeArrow is not arrow.");
            }
            cloneArrow.setVelocity(baseArrow.getVelocity());
            cloneArrow.setShooter(baseArrow.getShooter());
            cloneArrow.setFireTicks(baseArrow.getFireTicks());
            cloneArrow.setPierceLevel(baseArrow.getPierceLevel());
            cloneArrow.setKnockbackStrength(baseArrow.getKnockbackStrength());
            cloneArrow.setDamage(baseArrow.getDamage());
            cloneArrow.setCritical(baseArrow.isCritical());

            if (player.getGameMode() == GameMode.CREATIVE) {
                cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            } else if (bow == null || !Objects.requireNonNull(bow.getItemMeta()).hasEnchant(Enchantment.ARROW_INFINITE)) {
                nextConsumeArrow.setAmount(nextConsumeArrow.getAmount() - 1);
                cloneArrow.setPickupStatus(baseArrow.getPickupStatus());
            } else {
                cloneArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
        }
        final int consumeArrowAmount = consumeArrow.getAmount();
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> consumeArrow.setAmount(consumeArrowAmount),
                1L
        );
    }

}
