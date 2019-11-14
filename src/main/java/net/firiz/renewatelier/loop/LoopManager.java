/*
 * LoopManager.java
 *
 * Copyright (c) 2018 firiz.
 *
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 *
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package net.firiz.renewatelier.loop;

import java.util.ArrayList;
import java.util.List;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.item.drop.AnimatedDrop;
import net.firiz.renewatelier.version.packet.PacketUtils;
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
        animDrops = new ArrayList<>();
        loopRuns = new ArrayList<>();
        loopHalfSecRuns = new ArrayList<>();
        loopMiriRuns = new ArrayList<>();
        loopMinuteRuns = new ArrayList<>();
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
        return new ArrayList<>(animDrops);
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
            new ArrayList<>(animDrops).forEach(AnimatedDrop::anim);
            new ArrayList<>(loopMiriRuns).forEach(Runnable::run);
            if (period % 10 == 0) {
                halfSecLoop();
            }
            if (period > 20) {
                period = 0;
                secLoop();
                return;
            }
            period++;
        }, 0L, 1L);
    }

    private void halfSecLoop() {
        plugin.getServer().getWorlds().forEach(this::entityLoop);
        new ArrayList<>(loopHalfSecRuns).forEach(Runnable::run);
    }

    private void secLoop() {
        secPeriod++;

        new ArrayList<>(loopRuns).forEach(Runnable::run);

        int maxLength = 0;
        final List<Player> players = new ArrayList<>();
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
            final StringBuilder sb = new StringBuilder();
            final String name = player.getDisplayName();
            sb.append(name);
            for (int i = 0; i < maxLength - name.length() + 1; i++) {
                sb.append(" ");
            }
            sb.append(ChatColor.GREEN).append(PacketUtils.getPing(player));
            player.setPlayerListName(sb.toString());
        }

        if (secPeriod % 60 == 0) {
            secPeriod = 0;
            minuteLoop();
        }
    }

    private void minuteLoop() {
        new ArrayList<>(loopMinuteRuns).forEach(Runnable::run);
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
