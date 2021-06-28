package net.firiz.renewatelier.damage;

import net.firiz.ateliercommonapi.FakeId;
import net.firiz.ateliercommonapi.nms.entity.EntityData;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.player.CharSettings;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

final class HoloDamage {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    final void holoDamage(@NotNull LivingEntity victim, @Nullable Entity damager, List<DamageComponent> damages) {
        final Location location = victim.getEyeLocation();
        location.setY(location.getY() + GameConstants.HOLO_DAMAGE_POS);
        double allDamage = 0;
        for (int i = 0; i < damages.size(); i++) {
            final double damage = damages.get(i).getDamage();
            final AttackAttribute attribute = damages.get(i).getAttackAttribute();
            final int intDamage = (int) damage;
            final String viewDamage;
            if (attribute == AttackAttribute.HEAL && intDamage < 0) {
                viewDamage = String.valueOf(attribute.getColor()) + Math.abs(intDamage);
                allDamage += damage;
            } else if (intDamage <= 0) {
                viewDamage = ChatColor.WHITE + "Miss";
            } else {
                if (attribute.hasIcon()) {
                    viewDamage = String.valueOf(attribute.getColor()) + intDamage + ' ' + attribute.getIcon();
                } else {
                    viewDamage = String.valueOf(attribute.getColor()) + intDamage;
                }
                allDamage += damage;
            }
            final EntityData data = new EntityData(FakeId.createId(), EntityType.ARMOR_STAND);
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 20, 10, 20).stream().filter(e -> e instanceof Player).forEach(e -> {
                            final Player player = (Player) e;
                            final CharSettings settings = PlayerSaveManager.INSTANCE.getChar(player).getSettings();
                            final boolean showDamage = player == damager ? settings.isShowDamage() : settings.isShowOthersDamage();
                            if (showDamage) {
                                EntityData.armorStand(data, location.getWorld(), Component.text(viewDamage), true);
                                PacketUtils.sendPackets(player, data.packet(location));
                                Bukkit.getScheduler().runTaskLater(
                                        AtelierPlugin.getPlugin(),
                                        () -> PacketUtils.sendPacket(player, EntityPacket.despawnPacket(data.id())),
                                        10
                                );
                            }
                        });
                        location.setY(location.getY() + GameConstants.HOLO_DAMAGE_INTERVAL);
                    },
                    2L * i
            );
        }
        if (allDamage != 0) {
            if (aEntityUtils.hasLivingData(victim)) {
                final LivingData livingData = aEntityUtils.getLivingData(victim);
                livingData.damage(damager, allDamage);
            } else if (victim instanceof Player) {
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
