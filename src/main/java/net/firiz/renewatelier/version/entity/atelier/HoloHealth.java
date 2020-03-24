package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;

public class HoloHealth {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
    private final EntityLiving entity;
    private final String customName;

    private final LivingData livingData;
    private final boolean isBoss;

    // normal health bar
    private final FakeEntity holoHp;
    private final FakeEntity holoCustomName;
    private int holoDeleteTaskId = -1;

    // boss health bar
    private final BossBar bossBar;

    public HoloHealth(EntityLiving entity, String customName) {
        this.entity = entity;
        this.customName = customName;

        this.livingData = aEntityUtils.hasLivingData(entity) ? aEntityUtils.getLivingData(entity) : null;
        this.isBoss = this.livingData != null && this.livingData.hasStats() && this.livingData.getStats().isBoss();

        if (isBoss) {
            this.holoHp = null;
            this.holoCustomName = null;
            this.bossBar = Bukkit.createBossBar(customName, BarColor.RED, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        } else {
            final int holoId = -Randomizer.nextInt(Integer.MAX_VALUE);
            this.holoHp = new FakeEntity(holoId, FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            this.holoCustomName = new FakeEntity(holoId + 1, FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            this.bossBar = null;
        }
    }

    public void holo() {
        if (isBoss) {
            holoBoss();
        } else {
            holoNormal();
        }
    }

    private void holoNormal() {
        final LivingEntity bukkit = (LivingEntity) entity.getBukkitEntity();
        final StringBuilder displayHp = new StringBuilder(ChatColor.RED.toString());
        displayHp.append("❤❤❤❤❤❤❤❤❤");
        final double p = (bukkit.getHealth() / bukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) * 10;
        final int insertPos = (int) Math.floor(p);
        final double z = p - insertPos;
        if (z >= 0.5) {
            displayHp.insert(Math.min(2 + insertPos, displayHp.length()), ChatColor.YELLOW.toString());
            displayHp.insert(Math.min(5 + insertPos, displayHp.length()), ChatColor.GRAY.toString());
        } else {
            displayHp.insert(Math.min(2 + insertPos, displayHp.length()), ChatColor.GRAY.toString());
        }

        final BukkitScheduler scheduler = Bukkit.getScheduler();
        if (holoDeleteTaskId != -1) {
            scheduler.cancelTask(holoDeleteTaskId);
        }
        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getSpawnPacket(holoHp, getLoc(0)));
        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getSpawnPacket(holoCustomName, getLoc(1)));
        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getMessageStandMeta(bukkit.getWorld(), displayHp.toString(), true).compile(holoHp.getEntityId()));
        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getMessageStandMeta(bukkit.getWorld(), customName, true).compile(holoCustomName.getEntityId()));
        holoDeleteTaskId = scheduler.scheduleSyncDelayedTask(
                AtelierPlugin.getPlugin(),
                () -> {
                    ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getDespawnPacket(holoHp.getEntityId()));
                    ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getDespawnPacket(holoCustomName.getEntityId()));
                },
                20
        );

        for (int i = 0; i < 10; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getTeleportPacket(
                                holoHp.getEntityId(), getLoc(0), false
                        ));
                        ((WorldServer) entity.world).getChunkProvider().broadcast(entity, EntityPacket.getTeleportPacket(
                                holoCustomName.getEntityId(), getLoc(1), false
                        ));
                    },
                    2L * i
            );
        }
    }

    private void holoBoss() {

    }

    private Location getLoc(int y) {
        final LivingEntity bukkit = (LivingEntity) entity.getBukkitEntity();
        final Location nextLoc = bukkit.getEyeLocation().clone();
        nextLoc.setY(nextLoc.getY() + 0.2 + (0.3 * y));
        return nextLoc;
    }

}
