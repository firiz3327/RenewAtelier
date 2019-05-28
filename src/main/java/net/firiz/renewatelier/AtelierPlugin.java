/*
 * AtelierPlugin.java
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
package net.firiz.renewatelier;

import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.listener.BlockListener;
import net.firiz.renewatelier.listener.DebugListener;
import net.firiz.renewatelier.listener.InventoryListener;
import net.firiz.renewatelier.listener.PlayerListener;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.player.PlayerSaveManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.world.MyRoomManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author firiz
 */
public final class AtelierPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        removePlayerNPCStands();

        // registerEvents
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new DebugListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);

        // setup worlds
        Bukkit.getWorlds().forEach(AtelierPlugin::worldSettings);

        // setup managers
        MyRoomManager.INSTANCE.setup();
        ConfigManager.INSTANCE.reloadConfigs();
        SQLManager.INSTANCE.setup();
        LoopManager.INSTANCE.start();
        NPCManager.INSTANCE.setup();
        PlayerSaveManager.INSTANCE.loadPlayers();
    }

    public static void worldSettings(World world) {
        world.setAutoSave(true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    @Override
    public void onDisable() {
        LoopManager.INSTANCE.stopLoop();
        SQLManager.INSTANCE.close();
        NPCManager.INSTANCE.stop();
        removePlayerNPCStands();
    }

    @Override
    public void onLoad() {
//        final PluginLogger logger = new PluginLogger(this) {
//
//            final ConsoleReader console;
//
//            {
//                console = tryCatch(ConsoleReader.class, new Class[0]);
//            }
//
//            @Override
//            public void log(LogRecord logRecord) {
//                final AttributedStringBuilder builder = new AttributedStringBuilder();
//                builder.append("[")
//                        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
//                        .append("Atelier")
//                        .style(AttributedStyle.DEFAULT)
//                        .append("] ");
//                if (logRecord.getLevel() == Level.WARNING) {
//                    builder.style(
//                            AttributedStyle.DEFAULT
//                                    .background(AttributedStyle.RED)
//                                    .foreground(AttributedStyle.WHITE)
//                    );
//                }
////                    builder.append(logRecord.getMessage());
////                    console.print(builder.toAnsi());
////                    console.flush();
//                logRecord.setMessage(builder.toAnsi() + logRecord.getMessage());
//                try {
//                    final Method method = Logger.class.getMethod("log");
//                    method.invoke(this, logRecord);
//                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            <T> T tryCatch(Class<T> clasz, Class<?>[] clazz, Object... args) {
//                try {
//                    return clasz.getConstructor(clazz).newInstance(args);
//                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//                    e.printStackTrace();
//                    System.exit(1);
//                }
//                return null;
//            }
//        };
//        try {
//            final Field loggerField = JavaPlugin.class.getDeclaredField("logger");
//            loggerField.setAccessible(true);
//            loggerField.set(this, logger);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    private void removePlayerNPCStands() {
        getServer().getWorlds().stream().flatMap(
                world -> world.getEntitiesByClass(ArmorStand.class).stream()
        ).filter(stand -> !stand.isVisible()
                && stand.getCustomName() != null
                && stand.getCustomName().startsWith("npc,")
        ).forEachOrdered(Entity::remove);
    }

    public static AtelierPlugin getPlugin() {
        return AtelierPlugin.getPlugin(AtelierPlugin.class);
    }

}