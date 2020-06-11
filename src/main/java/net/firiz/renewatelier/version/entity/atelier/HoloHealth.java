package net.firiz.renewatelier.version.entity.atelier;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackResistance;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.utils.FakeId;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class HoloHealth {

    @NotNull
    private final LivingEntity entity;
    @NotNull
    private final String customName;

    @Nullable
    private final LivingData livingData;
    private final boolean isBoss;

    // normal health bar
    private final FakeEntity holoHp;
    private final FakeEntity holoCustomName;
    private final FakeEntity holoResistances;
    private int holoDeleteTaskId = -1;

    // boss health bar
    private final BossBar bossBar;

    public HoloHealth(@NotNull LivingEntity entity, @Nullable LivingData livingData, @NotNull String customName) {
        this.entity = entity;
        this.customName = customName;

        this.livingData = livingData;
        this.isBoss = this.livingData != null && this.livingData.hasStats() && this.livingData.getStats().isBoss();

        if (isBoss) {
            this.holoHp = null;
            this.holoCustomName = null;
            this.holoResistances = null;
            this.bossBar = Bukkit.createBossBar(customName, BarColor.RED, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        } else {
            this.holoHp = new FakeEntity(FakeId.createId(), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            this.holoCustomName = new FakeEntity(FakeId.createId(), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            this.holoResistances = new FakeEntity(FakeId.createId(), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
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

    public void die() {
        PacketUtils.broadcast(entity, EntityPacket.getDespawnPacket(holoHp.getEntityId()));
        PacketUtils.broadcast(entity, EntityPacket.getDespawnPacket(holoCustomName.getEntityId()));
        PacketUtils.broadcast(entity, EntityPacket.getDespawnPacket(holoResistances.getEntityId()));
    }

    private void holoNormal() {
        final StringBuilder displayHp = new StringBuilder(ChatColor.RED.toString());
        displayHp.append("❤❤❤❤❤❤❤❤❤");
        final double p = (entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) * 10;
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
        PacketUtils.broadcast(entity, EntityPacket.getSpawnPacket(holoHp, getLoc(0)));
        final boolean hasResistances;
        if (livingData != null && livingData.getStats() != null && !livingData.getStats().isEmptyResistances()) {
            hasResistances = true;
            PacketUtils.broadcast(entity, EntityPacket.getSpawnPacket(holoResistances, getLoc(1)));
            PacketUtils.broadcast(entity, EntityPacket.getMessageStandMeta(entity.getWorld(), createDisplayResistances(), true).compile(holoResistances.getEntityId()));
        } else {
            hasResistances = false;
        }
        final int customNameLoc = hasResistances ? 2 : 1;
        PacketUtils.broadcast(entity, EntityPacket.getSpawnPacket(holoCustomName, getLoc(customNameLoc)));
        PacketUtils.broadcast(entity, EntityPacket.getMessageStandMeta(entity.getWorld(), displayHp.toString(), true).compile(holoHp.getEntityId()));
        PacketUtils.broadcast(entity, EntityPacket.getMessageStandMeta(entity.getWorld(), createDisplayCustomName(), true).compile(holoCustomName.getEntityId()));
        holoDeleteTaskId = scheduler.scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), this::die, 20);

        for (int i = 0; i < 10; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        if (entity.isDead()) {
                            die();
                        } else {
                            PacketUtils.broadcast(entity, EntityPacket.getTeleportPacket(
                                    holoHp.getEntityId(), getLoc(0), false
                            ));
                            PacketUtils.broadcast(entity, EntityPacket.getTeleportPacket(
                                    holoCustomName.getEntityId(), getLoc(customNameLoc), false
                            ));
                            if (hasResistances) {
                                PacketUtils.broadcast(entity, EntityPacket.getTeleportPacket(
                                        holoResistances.getEntityId(), getLoc(1), false
                                ));
                            }
                        }
                    },
                    2L * i
            );
        }
    }

    private void holoBoss() {

    }

    private String createDisplayResistances() {
        Objects.requireNonNull(livingData);
        Objects.requireNonNull(livingData.getStats());

        final StringBuilder sb = new StringBuilder();
        final MonsterStats status = livingData.getStats();
        status.getBuffResistances().entrySet().stream()
                .filter(entry -> entry.getValue() != AttackResistance.NONE)
                .forEach(entry -> sb.append(entry.getKey().getIcon()).append(entry.getValue().getIcon()));
        return sb.toString();
    }

    private String createDisplayCustomName() {
        final StringBuilder sb = new StringBuilder(customName);
        if (livingData != null && livingData.hasStats()) {
            sb.setLength(0);
            final EntityStatus status = livingData.getStats();
            assert status != null;

            final Set<String> iconSet = new TreeSet<>();
            status.getBuffs().forEach(buff -> iconSet.add(buff.getType().getWord(buff.getX() > 0)));
            sb.append("Lv.").append(status.getLevel());
            if (!iconSet.isEmpty()) {
                sb.append(" ");
                iconSet.forEach(sb::append);
            }
        }
        return sb.toString();
    }

    private Location getLoc(int y) {
        final Location nextLoc = entity.getEyeLocation().clone();
        nextLoc.setY(nextLoc.getY() + GameConstants.HOLO_HEALTH_POS + (GameConstants.HOLO_HEALTH_INTERVAL * y));
        return nextLoc;
    }

}
