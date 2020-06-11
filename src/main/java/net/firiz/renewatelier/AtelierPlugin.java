package net.firiz.renewatelier;

import net.firiz.renewatelier.command.CheckCommand;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.entity.EntityCleanUp;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.listener.*;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.tab.TabList;
import net.firiz.renewatelier.world.MyRoomManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author firiz
 */
public final class AtelierPlugin extends JavaPlugin {

    private InventoryManager inventoryManager;
    private TabList tabList;

    @Override
    public void onEnable() {
        inventoryManager = new InventoryManager();
        removePlayerNPCStands();

        // registerEvents
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ServerListener(), this);
        pluginManager.registerEvents(new DebugListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new InventoryListener(this), this);

        Objects.requireNonNull(getCommand("check")).setExecutor(new CheckCommand());

        // setup worlds
        Bukkit.getWorlds().forEach(AtelierPlugin::worldSettings);

        // setup managers and singleton classes
        MyRoomManager.INSTANCE.setup();
        ConfigManager.INSTANCE.reloadConfigs();
        SQLManager.INSTANCE.setup();
        LoopManager.INSTANCE.start();
        NPCManager.INSTANCE.load();
        PlayerSaveManager.INSTANCE.loadPlayers();
        /*
        AtelierEntityUtils.INSTANCE
        KettleItemManager.INSTANCE
        KettleBonusManager.INSTANCE
        ScriptManager.INSTANCE
         */

        ReplaceVanillaItems.changeRecipe();
        tabList = new TabList();
        tabList.init();

        LoopManager.INSTANCE.addSec(new EntityCleanUp());
        LoopManager.INSTANCE.addSec(NPCManager.INSTANCE.lookEyeLoop());
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
        resetEntities();
        removePlayerNPCStands();
    }

    private void resetEntities() {
        final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;
        getServer().getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).kickPlayer("server reloading.");
            } else if (entity instanceof LivingEntity && aEntityUtils.hasLivingData(entity)) {
                entity.remove();
            }
        }));
    }

    private void removePlayerNPCStands() {
        getServer().getWorlds().stream().flatMap(
                world -> world.getEntitiesByClass(ArmorStand.class).stream()
        ).filter(stand -> !stand.isVisible()
                && stand.getCustomName() != null
                && stand.getCustomName().startsWith("npc,")
        ).forEach(Entity::remove);
    }

    public static AtelierPlugin getPlugin() {
        return getPlugin(AtelierPlugin.class);
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public TabList getTabList() {
        return tabList;
    }
}
