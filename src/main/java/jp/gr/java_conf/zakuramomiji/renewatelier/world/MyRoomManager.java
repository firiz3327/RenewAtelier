/*
 * MyRoomManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.world;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author firiz
 */
public enum MyRoomManager {
    INSTANCE;

    private final String world_name = "atelier_world";
    private final File map_folder = new File(
            AtelierPlugin.getPlugin().getDataFolder(), "rooms"
    );
    private final int island_distance = 50;
    private World world;

    public void setup() {
        final World worldTemp = Bukkit.getWorld(world_name);
        world = worldTemp != null ? worldTemp : new WorldCreator(world_name).generator(new ChunkGenerator() {
            @Override
            public ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int chunkx, final int chunkz, final ChunkGenerator.BiomeGrid biome) {
                final ChunkGenerator.ChunkData data = createChunkData(world);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        biome.setBiome(x, z, Biome.PLAINS);
                    }
                }
                return data;
            }
        }).createWorld();
        world.setAutoSave(true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

    public void warpRoom(final Player player) {
        warpRoom(player, player.getUniqueId());
    }

    public void warpRoom(final Player warp_player, final UUID island_uuid) {
        if (hasRoom(island_uuid)) {
            Chore.warp(warp_player, getRoom(island_uuid));
        }
    }

    public Location getRoom(final UUID uuid) {
        try {
            for (final File file : map_folder.listFiles()) {
                final List<String> readLines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                if (UUID.fromString(readLines.get(0)).equals(uuid)) {
                    final String[] loc_str = file.getName().split(",");
                    return new Location(
                            world,
                            Integer.parseInt(loc_str[0]) + 0.5,
                            61,
                            Integer.parseInt(loc_str[1]) + 0.5
                    );
                }
            }
        } catch (IOException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean hasRoom(final UUID uuid) {
        try {
            for (final File file : map_folder.listFiles()) {
                final List<String> readLines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                if (UUID.fromString(readLines.get(0)).equals(uuid)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void createRoom(final UUID uuid) {
        final File file = createFile();
        try {
            FileUtils.write(file, uuid.toString(), StandardCharsets.UTF_8);
            final String[] loc_str = file.getName().split(",");
            final Location loc = new Location(
                    world,
                    Integer.parseInt(loc_str[0]),
                    60,
                    Integer.parseInt(loc_str[1])
            );
            createDefaultRoom(loc);
        } catch (IOException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    private void createDefaultRoom(final Location loc) {
        int locy = loc.getBlockY() - 1;
        final int defx = loc.getBlockX() + 7;
        final int defz = loc.getBlockZ() + 7;
//        world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).setType(Material.BEDROCK);
        for (int n = 0; n < 20; n++) {
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    final Block block = world.getBlockAt(defx - i, locy + n, defz - j);
                    block.setType(Material.AIR);
                    if (n == 0 || n == 19) { // bedrock bottom top
                        block.setType(Material.BEDROCK);
                    } else if (i == 0 || i == 14) { // bedrock wall 1
                        block.setType(Material.BEDROCK);
                    } else if (j == 0 || j == 14) { // bedrock wall 2
                        block.setType(Material.BEDROCK);
                    } else if (n == 1 || n == 18) { // wood bottom top
                        block.setType(Material.OAK_PLANKS);
                    } else if (i == 1 || i == 13) { // wood wall 1
                        block.setType(Material.OAK_PLANKS);
                    } else if (j == 1 || j == 13) { // wood wall 2
                        block.setType(Material.OAK_PLANKS);
                    }
                }
            }
        }
    }

    private File createFile() {
        int distance = 2;
        int x = 0;
        int y = 0;
        int angle = 0;
        while (true) {
            final File f = new File(map_folder, x + "," + y);
            if (f.exists()) {
                for (int i = 0; i < (int) (distance / 2); i++) {
                    switch (angle) {
                        case 0: // up
                            y += island_distance;
                            break;
                        case 1: // right
                            x += island_distance;
                            break;
                        case 2: // down
                            y -= island_distance;
                            break;
                        case 3: // left
                            x -= island_distance;
                            break;
                    }
                    final File f2 = new File(map_folder, x + "," + y);
                    if (!f2.exists()) {
                        try {
                            f2.createNewFile();
                        } catch (IOException ex) {
                            Chore.log(Level.SEVERE, null, ex);
                        }
                        return f2;
                    }
                }
                if (angle != 3) {
                    angle++;
                    distance++;
                } else {
                    angle = 0;
                    distance++;
                }
            } else {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Chore.log(Level.SEVERE, null, ex);
                }
                return f;
            }
        }
    }

}
