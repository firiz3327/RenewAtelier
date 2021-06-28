package net.firiz.renewatelier.version.entity.atelier.holo;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.firiz.ateliercommonapi.FakeId;
import net.firiz.ateliercommonapi.nms.entity.EntityData;
import net.firiz.ateliercommonapi.nms.packet.EntityPacket;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.damage.AttackResistance;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.util.WeakCollection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class HoloHealth extends AbstractHoloHealth {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private final EntityData holoHp;
    private final EntityData holoCustomName;
    private final EntityData holoResistances;
    private final WeakCollection<Player> sentPlayers = new WeakCollection<>();
    private final TeleportTaskList teleportTaskList = new TeleportTaskList();
    private int holoDeleteTaskId = -1;
    private boolean hasResistances;

    public HoloHealth(@NotNull LivingEntity entity, @Nullable LivingData livingData, @NotNull String customName) {
        super(entity, livingData, customName);
        this.holoHp = new EntityData(FakeId.createId(), EntityType.ARMOR_STAND);
        this.holoCustomName = new EntityData(FakeId.createId(), EntityType.ARMOR_STAND);
        this.holoResistances = new EntityData(FakeId.createId(), EntityType.ARMOR_STAND);
    }

    @Override
    public void holo() {
        if (holoDeleteTaskId != -1) {
            SCHEDULER.cancelTask(holoDeleteTaskId);
        }

        final Set<Player> startPlayer = new ObjectOpenHashSet<>();
        PacketUtils.trackPlayer(entity).stream().filter(player -> !sentPlayers.contains(player)).forEach(startPlayer::add);
        final boolean hasResistancesTemp = livingData != null && livingData.getStats() != null && !livingData.getStats().isEmptyResistances();
        final boolean changedResistances = hasResistances != hasResistancesTemp;
        hasResistances = hasResistancesTemp;
        if (!startPlayer.isEmpty()) {
            final List<Packet<?>> spawn = spawn();
            startPlayer.forEach(player -> {
                sentPlayers.add(player);
                PacketUtils.sendPackets(player, spawn);
            });
        }
        sentPlayers.forEach(player -> {
            final Packet<?> packet = holoResSpawn(changedResistances);
            if (packet != null) {
                PacketUtils.sendPackets(player, packet);
            }
            PacketUtils.sendPackets(player, meta());
        });

        holoDeleteTaskId = SCHEDULER.runTaskLater(AtelierPlugin.getPlugin(), this::die, 20L).getTaskId();
        teleportTaskList.cancel();
        for (int i = 0; i < 10; i++) {
            teleportTaskList.add(Bukkit.getScheduler().runTaskLater(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        if (entity.isDead()) {
                            die();
                        } else {
                            PacketUtils.broadcast(entity, entity.getWorld(), EntityPacket.teleportPacket(holoHp.id(), getLoc(0), false));
                            PacketUtils.broadcast(entity, entity.getWorld(), EntityPacket.teleportPacket(holoCustomName.id(), getLoc(hasResistances ? 2 : 1), false));
                            if (hasResistances) {
                                PacketUtils.broadcast(entity, entity.getWorld(), EntityPacket.teleportPacket(holoResistances.id(), getLoc(1), false));
                            }
                        }
                    },
                    2L * i
            ).getTaskId());
        }
    }

    private List<Packet<?>> spawn() {
        final List<Packet<?>> packets = new ObjectArrayList<>();
        packets.add(holoHp.spawnPacket(getLoc(0)));
        final Packet<?> packet = holoResSpawn(true);
        if (packet != null) {
            packets.add(packet);
        }
        packets.add(holoCustomName.spawnPacket(getLoc(hasResistances ? 2 : 1)));
        return packets;
    }

    private Packet<?> holoResSpawn(boolean changed) {
        if (hasResistances && changed) {
            return holoResistances.spawnPacket(getLoc(1));
        }
        return null;
    }

    private List<Packet<?>> meta() {
        final List<Packet<?>> packets = new ObjectArrayList<>();
        if (hasResistances) {
            packets.add(EntityData.armorStand(holoResistances, entity.getWorld(), Component.text(createDisplayResistances()), true).metaPacket());
        }
        final StringBuilder displayHp = new StringBuilder(ChatColor.RED.toString());
        displayHp.append("❤❤❤❤❤❤❤❤❤");
        final double p = getPercentHealth() * 10;
        final int insertPos = (int) Math.floor(p);
        final double z = p - insertPos;
        if (z >= 0.5) {
            displayHp.insert(Math.min(2 + insertPos, displayHp.length()), ChatColor.YELLOW);
            displayHp.insert(Math.min(5 + insertPos, displayHp.length()), ChatColor.GRAY);
        } else {
            displayHp.insert(Math.min(2 + insertPos, displayHp.length()), ChatColor.GRAY);
        }
        packets.add(EntityData.armorStand(holoHp, entity.getWorld(), Component.text(displayHp.toString()), true).metaPacket());
        packets.add(EntityData.armorStand(holoCustomName, entity.getWorld(), Component.text(createDisplayCustomName()), true).metaPacket());
        return packets;
    }

    @Override
    public void die() {
        sentPlayers.clear();
        teleportTaskList.cancel();
        PacketUtils.broadcast(entity, entity.getWorld(), holoHp.despawnPacket());
        PacketUtils.broadcast(entity, entity.getWorld(), holoCustomName.despawnPacket());
        PacketUtils.broadcast(entity, entity.getWorld(), holoResistances.despawnPacket());
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

    private static class TeleportTaskList extends IntArrayList {

        public void cancel() {
            for (int i : this) {
                SCHEDULER.cancelTask(i);
            }
            clear();
        }

    }

}
