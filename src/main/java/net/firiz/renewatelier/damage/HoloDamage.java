package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.entity.player.CharSettings;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class HoloDamage {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    @SafeVarargs
    final void holoDamage(@NotNull LivingEntity victim, @Nullable Entity damager, DamageComponent... damages) {
        holoDamage(victim, damager, Arrays.asList(damages));
    }

    final void holoDamage(@NotNull LivingEntity victim, @Nullable Entity damager, List<DamageComponent> damages) {
        final Location location = victim.getEyeLocation();
        location.setY(location.getY() + 1.2);
        double allDamage = 0;
        for (int i = 0; i < damages.size(); i++) {
            final double damage = damages.get(i).getDamage();
            final AttackAttribute attribute = damages.get(i).getAttackAttribute();
            final int intDamage = (int) damage;
            final String viewDamage;
            if (intDamage <= 0) { // 1.0 ダメージ以上でないとダメージ換算なし
                viewDamage = ChatColor.WHITE + "Miss";
            } else {
                viewDamage = String.valueOf(attribute.getColor()) + intDamage + ' ' + attribute.getIcon();
                allDamage += damage;
            }
            final FakeEntity fakeEntity = new FakeEntity(-Randomizer.nextInt(Integer.MAX_VALUE), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 20, 10, 20).stream().filter(e -> e instanceof Player).forEach(e -> {
                            final Player player = (Player) e;
                            final CharSettings settings = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId()).getSettings();
                            final boolean showDamage = player == damager ? settings.isShowDamage() : settings.isShowOthersDamage();
                            if (showDamage) {
                                PacketUtils.sendPacket(player, EntityPacket.getSpawnPacket(fakeEntity, location));
                                PacketUtils.sendPacket(player, EntityPacket.getMessageStandMeta(player.getWorld(), viewDamage, true).compile(fakeEntity.getEntityId()));
                                Bukkit.getScheduler().runTaskLater(
                                        AtelierPlugin.getPlugin(),
                                        () -> PacketUtils.sendPacket(player, EntityPacket.getDespawnPacket(fakeEntity.getEntityId())),
                                        10
                                );
                            }
                        });
                        location.setY(location.getY() + 0.3);
                    },
                    2L * i
            );
        }
        if (allDamage > 0) {
            if (aEntityUtils.hasLivingData(victim)) {
                final LivingData livingData = aEntityUtils.getLivingData(victim);
                livingData.damage(damager, allDamage);
            } else if(victim instanceof Player) {
                final CharStats charStats = psm.getChar(victim.getUniqueId()).getCharStats();
                charStats.damageHp(allDamage);
            } else {
                victim.setHealth(Math.max(0, victim.getHealth() - allDamage));
                victim.setLastDamageCause(damager == null ? new EntityDamageEvent(
                        victim,
                        EntityDamageEvent.DamageCause.CUSTOM,
                        allDamage
                ) : new EntityDamageByEntityEvent(
                        victim,
                        damager,
                        EntityDamageEvent.DamageCause.CUSTOM,
                        allDamage
                ));
            }
        }
    }
}
