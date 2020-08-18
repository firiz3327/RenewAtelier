package net.firiz.renewatelier.world;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.chores.ItemUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author firiz
 */
public enum MyRoomManager {
    INSTANCE; // enum singleton style

    private final File mapFolder = new File(
            AtelierPlugin.getPlugin().getDataFolder(), "rooms"
    );
    private World world;

    public void setup() {
        final String world_name = "atelier_world";
        final World worldTemp = Bukkit.getWorld(world_name);
        world = (worldTemp != null) ? worldTemp : new WorldCreator(world_name).generator(
                new ChunkGenerator() {
                    @NotNull
                    @Override
                    public ChunkData generateChunkData(@NotNull final World world, @NotNull final Random random, final int chunkX, final int chunkZ, @NotNull final BiomeGrid biome) {
                        final ChunkData data = createChunkData(world);
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int y = 0; y <= 255; y++) {
                                    biome.setBiome(x, y, z, Biome.PLAINS);
                                }
                            }
                        }
                        return data;
                    }
                }
        ).createWorld();
        AtelierPlugin.worldSettings(world);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

    public void warpRoom(final Player player) {
        warpRoom(player, player.getUniqueId());
    }

    public void warpRoom(final Player warpPlayer, final UUID islandUuid) {
        if (hasRoom(islandUuid)) {
            ItemUtils.warp(warpPlayer, getRoom(islandUuid));
        }
    }

    public Location getRoom(final UUID uuid) {
        try {
            for (final File file : mapFolder.listFiles()) {
                if (!file.getName().contains(".")) {
                    final List<String> readLines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                    if (UUID.fromString(readLines.get(0)).equals(uuid)) {
                        final String[] locStr = file.getName().split(",");
                        return new Location(
                                world,
                                Integer.parseInt(locStr[0]) + 0.5,
                                61,
                                Integer.parseInt(locStr[1]) + 0.5
                        );
                    }
                }
            }
        } catch (IOException ex) {
            CommonUtils.logWarning(ex);
        }
        return null;
    }

    public boolean hasRoom(final UUID uuid) {
        try {
            for (final File file : mapFolder.listFiles()) {
                if (!file.getName().contains(".")) {
                    final List<String> readLines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                    if (UUID.fromString(readLines.get(0)).equals(uuid)) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            CommonUtils.logWarning(ex);
        }
        return false;
    }

    public void createRoom(final UUID uuid) {
        final File file = createFile();
        try {
            FileUtils.write(file, uuid.toString(), StandardCharsets.UTF_8);
            final String[] locStr = file.getName().split(",");
            final Location loc = new Location(
                    world,
                    Integer.parseInt(locStr[0]),
                    60,
                    Integer.parseInt(locStr[1])
            );
            createDefaultRoom(loc);
        } catch (IOException ex) {
            CommonUtils.logWarning(ex);
        }
    }

    private void createDefaultRoom(final Location loc) {
        int locy = loc.getBlockY() - 1;
        final int defx = loc.getBlockX() + 7;
        final int defz = loc.getBlockZ() + 7;
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
        final int island_distance = 20;
        int distance = 2;
        int x = 0;
        int y = 0;
        int angle = 0;
        while (true) {
            final File f = new File(mapFolder, x + "," + y);
            if (f.exists()) {
                for (int i = 0; i < (distance / 2); i++) {
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
                        default:
                            break;
                    }
                    final File f2 = new File(mapFolder, x + "," + y);
                    if (!f2.exists()) {
                        try {
                            f2.createNewFile();
                        } catch (IOException ex) {
                            CommonUtils.logWarning(ex);
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
                    CommonUtils.logWarning(ex);
                }
                return f;
            }
        }
    }

}
