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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.Cauldron;

/**
 * @author firiz
 */
public enum LoopManager {
    INSTANCE; // enum singleton style

    private final AtelierPlugin plugin = AtelierPlugin.getPlugin();
    private final List<AnimatedDrop> animDrops;
    private final List<Runnable> loop_runs;
    private final List<Runnable> loop_half_sec_runs;
    private final List<Runnable> loop_miri_runs;
    private boolean start;
    private int period = 0;
    private int sec_period = 0;
    private int taskid;

    LoopManager() {
        start = false;
        animDrops = new ArrayList<>();
        loop_runs = new ArrayList<>();
        loop_half_sec_runs = new ArrayList<>();
        loop_miri_runs = new ArrayList<>();
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
        plugin.getServer().getScheduler().cancelTask(taskid);
        animDrops.stream().filter((ad) -> (!ad.isGet())).forEachOrdered((ad) -> ad.getDrop().remove());
    }

    public void addLoopEffect(final Runnable run) {
        loop_runs.add(run);
    }

    public void removeLoopEffect(final Runnable run) {
        loop_runs.remove(run);
    }

    public void addLoopEffectHalfSec(final Runnable run) {
        loop_half_sec_runs.add(run);
    }

    public void removeLoopEffectHalfSec(final Runnable run) {
        loop_half_sec_runs.remove(run);
    }

    public void addLoopEffectMiri(final Runnable run) {
        loop_miri_runs.add(run);
    }

    public void removeLoopEffectMiri(final Runnable run) {
        loop_miri_runs.remove(run);
    }

    // 厳密さは求めていないので大分適当。あっているかはわからない
    private void loop() {
        taskid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            new ArrayList<>(animDrops).forEach(AnimatedDrop::anim);
            new ArrayList<>(loop_miri_runs).forEach(Runnable::run);
            if (period % 10 == 0) {
                half_sec_loop();
            }
            if (period > 20) {
                period = 0;
                sec_loop();
                return;
            }
            period++;
        }, 0L, 1L);
    }

    private void half_sec_loop() {
        plugin.getServer().getWorlds().forEach(this::entityLoop);
        new ArrayList<>(loop_half_sec_runs).forEach(Runnable::run);
    }

    private void sec_loop() {
        sec_period++;

        new ArrayList<>(loop_runs).forEach(Runnable::run);

        int max_length = 0;
        final List<Player> players = new ArrayList<>();
        for (final World world : Bukkit.getWorlds()) {
            for (final Player player : world.getPlayers()) {
                players.add(player);
                final int length = player.getDisplayName().length();
                if (max_length < length) {
                    max_length = length;
                }
            }
        }

        for (final Player player : players) {
            final StringBuilder sb = new StringBuilder();
            final String name = player.getDisplayName();
            sb.append(name);
            for (int i = 0; i < max_length - name.length() + 1; i++) {
                sb.append(" ");
            }
            sb.append(ChatColor.GREEN).append(PacketUtils.getPing(player));
            player.setPlayerListName(sb.toString());
        }

        if (sec_period - 60 == 0) {
            sec_period = 0;
            minute_loop();
        }
    }

    private void minute_loop() {

    }

    private void cauldronDamage(final Entity entity) {
        final Block block = entity.getLocation().getBlock();
        if (block.getType() == Material.CAULDRON) {
            if (((Cauldron) block.getState().getData()).isFull()) {
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

            /*
            switch (entity.getType()) {
                case PLAYER: {
                    Player player = (Player) entity;
                    for (ItemStack item : player.getInventory().getContents()) {
//                        amm.setCheck(item);
                    }
                    if (player.getLocation()
                            .getBlock()
                            .getRelative(BlockFace.DOWN)
                            .getY() <= -5) {
                        player.teleport(player.getWorld().getSpawnLocation());
                    }
                    ItemStack hand = player.getInventory().getItemInMainHand();
                    boolean hasSlow = player.hasPotionEffect(PotionEffectType.SLOW_DIGGING);
                    if (Chore.isWand(hand)) {
                        if (!hasSlow) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
                        }
                    } else if (hasSlow) {
                        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    }
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            if(player.getGameMode() != GameMode.CREATIVE) {
//                                PlayerInventory playerInv = player.getInventory();
//                                while (true) {
//                                    if (playerInv.contains(Material.BARRIER)) {
//                                        playerInv.remove(Material.BARRIER);
//                                        continue;
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }.runTaskLater(plugin, 1);
                    break;
                }
                case DROPPED_ITEM: {
//                    final Item item = (Item) entity;
//                    amm.setCheck(item.getItemStack());
//                    Block block = entity.getLocation().getBlock();
//                    if (ak.isAlchemyKettle(block)) {
//                        world.playSound(block.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 0.1F, 1);
//                        entity.remove();
//                    }
                    break;
                }
            }
             */
        }
    }

}
