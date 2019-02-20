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
package jp.gr.java_conf.zakuramomiji.renewatelier;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.config.ConfigManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.listener.BlockListener;
import jp.gr.java_conf.zakuramomiji.renewatelier.listener.DebugListener;
import jp.gr.java_conf.zakuramomiji.renewatelier.listener.InventoryListener;
import jp.gr.java_conf.zakuramomiji.renewatelier.listener.PlayerListener;
import jp.gr.java_conf.zakuramomiji.renewatelier.loop.LoopManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.engine.GraalPy3Engine;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.PacketUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.world.MyRoomManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author firiz
 */
public final class AtelierPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // remove playernpc stands
        getServer().getWorlds().forEach((world) -> {
            world.getEntitiesByClass(ArmorStand.class).stream().filter(
                    (stand) -> (!stand.isVisible()
                    && stand.getCustomName() != null
                    && stand.getCustomName().startsWith("npc,"))
            ).forEachOrdered((stand) -> {
                stand.remove();
            });
        });

        // registerEvents
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new DebugListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);

        // setup worlds
        Bukkit.getWorlds().stream().forEachOrdered((World world) -> {
            world.setAutoSave(true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });

        // init PacketUtils
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketUtils.init(protocolManager);

        // setup managers
        MyRoomManager.INSTANCE.setup();
        ConfigManager.INSTANCE.reloadConfigs();
        SQLManager.INSTANCE.setup();
        LoopManager.INSTANCE.start();
        NPCManager.INSTANCE.setup();
        PlayerSaveManager.INSTANCE.loadPlayers();

        GraalPy3Engine pyEngine = new GraalPy3Engine();
        try {
            pyEngine.eval("status = -1\ndef test():\n    global status\n    status += 1\n    print(status)");
            ((Invocable) pyEngine).invokeFunction("test");
        } catch (ScriptException ex) {
            Logger.getLogger(AtelierPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AtelierPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onDisable() {
        LoopManager.INSTANCE.stopLoop();
        SQLManager.INSTANCE.close();
        NPCManager.INSTANCE.stop();
    }

    public static AtelierPlugin getPlugin() {
        return AtelierPlugin.getPlugin(AtelierPlugin.class);
    }

}
