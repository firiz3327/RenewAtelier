package net.firiz.renewatelier;

import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.renewatelier.command.CheckCommand;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.entity.EntityCleanUp;
import net.firiz.renewatelier.listener.*;
import net.firiz.renewatelier.listener.player.PlayerListener;
import net.firiz.renewatelier.loop.AnimatedDropManager;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.entity.atelier.TargetEntityTypes;
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

    private final TabList tabList;

    public AtelierPlugin() {
        tabList = new TabList();
    }

    @Override
    public void onEnable() {
        CommonUtils.log("0 onEnable [Done]");
        removePlayerNPCStands();
        CommonUtils.log("1 remove player npc [Done]");

        // registerEvents
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ServerListener(), this);
        pluginManager.registerEvents(new DebugListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        CommonUtils.log("2 register events [Done]");

        Objects.requireNonNull(getCommand("check")).setExecutor(new CheckCommand());
        CommonUtils.log("3 command set executor [Done]");

        // setup worlds
        Bukkit.getWorlds().forEach(AtelierPlugin::worldSettings);
        CommonUtils.log("4 setup world [Done]");

        // setup managers and singleton classes
        MyRoomManager.INSTANCE.setup();
        CommonUtils.log("5 setup my room manager [Done]");
        ConfigManager.INSTANCE.reloadConfigs();
        CommonUtils.log("6 load config [Done]");
        SQLManager.INSTANCE.setup();
        CommonUtils.log("7 setup sql [Done]");
        NPCManager.INSTANCE.load();
        CommonUtils.log("8 load npc [Done]");
        PlayerSaveManager.INSTANCE.loadPlayers();
        CommonUtils.log("9 load players [Done]");

        ReplaceVanillaItems.changeRecipe();
        CommonUtils.log("10 change recipe [Done]");
        tabList.init();
        CommonUtils.log("11 init tab list [Done]");

        AnimatedDropManager.INSTANCE.start();
        CommonUtils.log("12 start animated drop [Done]");
        LoopManager.INSTANCE.addSeconds(new EntityCleanUp());
        CommonUtils.log("13 add loop entity clean up [Done]");
        LoopManager.INSTANCE.addSeconds(NPCManager.INSTANCE.npcLoop());
        CommonUtils.log("14 add loop npc loop [Done]");

        TargetEntityTypes.check();
        CommonUtils.log("15 target entity type check [Done]");
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

    public TabList getTabList() {
        return tabList;
    }
}
