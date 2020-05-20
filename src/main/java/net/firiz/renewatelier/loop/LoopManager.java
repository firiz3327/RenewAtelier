package net.firiz.renewatelier.loop;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.item.drop.AnimatedDrop;
import net.firiz.renewatelier.version.packet.PacketUtils;
import net.firiz.renewatelier.version.packet.PayloadPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public enum LoopManager {
    INSTANCE; // enum singleton style

    private final AtelierPlugin plugin = AtelierPlugin.getPlugin();
    private final List<AnimatedDrop> animDrops;
    private final List<Runnable> loopRuns;
    private final List<Runnable> loopHalfSecRuns;
    private final List<Runnable> loopMiriRuns;
    private final List<Runnable> loopMinuteRuns;
    private boolean start;
    private int period = 0;
    private int secPeriod = 0;
    private int taskId;

    LoopManager() {
        start = false;
        animDrops = new ObjectArrayList<>();
        loopRuns = new ObjectArrayList<>();
        loopHalfSecRuns = new ObjectArrayList<>();
        loopMiriRuns = new ObjectArrayList<>();
        loopMinuteRuns = new ObjectArrayList<>();
    }

    public void start() {
        if (!start) {
            loop();
            start = true;
        }
    }

    public void addAnimatedDrop(AnimatedDrop drop) {
        animDrops.add(drop);
    }

    public void removeAnimatedDrop(AnimatedDrop drop) {
        animDrops.remove(drop);
    }

    public List<AnimatedDrop> getAnimDrops() {
        return new ObjectArrayList<>(animDrops);
    }

    public void stopLoop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
        animDrops.stream().filter(ad -> (!ad.isGet())).forEachOrdered(ad -> ad.getDrop().remove());
    }

    public void addSec(final Runnable run) {
        loopRuns.add(run);
    }

    public void removeSec(final Runnable run) {
        loopRuns.remove(run);
    }

    public void addMiri(final Runnable run) {
        loopMiriRuns.add(run);
    }

    public void removeMiri(final Runnable run) {
        loopMiriRuns.remove(run);
    }

    public void addMinutes(final Runnable run) {
        loopMinuteRuns.add(run);
    }

    public void removeMinutes(final Runnable run) {
        loopMinuteRuns.remove(run);
    }

    // 厳密さは求めていないので大分適当。あっているかはわからない
    private void loop() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            new ObjectArrayList<>(animDrops).forEach(AnimatedDrop::anim);
            new ObjectArrayList<>(loopMiriRuns).forEach(Runnable::run);
            if (period % 10 == 0) {
                halfSecLoop();
            }
            if (period >= 20) {
                period = 0;
                secLoop();
                return;
            }
            period++;
        }, 0L, 1L);
    }

    private void halfSecLoop() {
        plugin.getServer().getWorlds().forEach(this::entityLoop);
        new ObjectArrayList<>(loopHalfSecRuns).forEach(Runnable::run);
    }

    private void secLoop() {
        secPeriod++;

        new ObjectArrayList<>(loopRuns).forEach(Runnable::run);

        int maxLength = 0;
        final List<Player> players = new ObjectArrayList<>();
        for (final World world : Bukkit.getWorlds()) {
            for (final Player player : world.getPlayers()) {
                players.add(player);
                final int length = player.getDisplayName().length();
                if (maxLength < length) {
                    maxLength = length;
                }
            }
        }

        for (final Player player : players) {
            PayloadPacket.sendBrand(player);
            final StringBuilder sb = new StringBuilder();
            final String name = player.getDisplayName();
            sb.append(name);
            for (int i = 0; i < maxLength - name.length() + 1; i++) {
                sb.append(" ");
            }
            sb.append(ChatColor.GREEN).append(PacketUtils.getPing(player));
            PayloadPacket.sendBrand(player);
            player.setPlayerListName(sb.toString());
        }

        if (secPeriod % 60 == 0) {
            secPeriod = 0;
            minuteLoop();
        }
    }

    private void minuteLoop() {
        new ObjectArrayList<>(loopMinuteRuns).forEach(Runnable::run);
    }

    private void cauldronDamage(final Entity entity) {
        final Block block = entity.getLocation().getBlock();
        if (block.getType() == Material.CAULDRON) {
            final Levelled levelled = ((Levelled) block.getBlockData());
            if (levelled.getLevel() == levelled.getMaximumLevel()) {
                final Location loc = block.getLocation();
                loc.setY(loc.getY() - 1);
                if (loc.getBlock().getType() == Material.FIRE) {
                    ((LivingEntity) entity).damage(1);
                }
            }
        }
    }

    private void entityLoop(final World world) {
        for (final Entity entity : world.getEntities()) {
            if (entity instanceof LivingEntity) {
                cauldronDamage(entity);
            }
        }
    }

}
