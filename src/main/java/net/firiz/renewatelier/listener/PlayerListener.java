package net.firiz.renewatelier.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableEntityInventory;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.kettle.inventory.AlchemyKettleInventory;
import net.firiz.renewatelier.alchemy.kettle.inventory.RecipeSelectInventory;
import net.firiz.renewatelier.entity.arrow.ArrowManager;
import net.firiz.renewatelier.event.AsyncPlayerInteractEntityEvent;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.quest.book.QuestBook;
import net.firiz.renewatelier.script.ScriptItem;
import net.firiz.renewatelier.version.inject.PlayerInjection;
import net.firiz.renewatelier.version.packet.PayloadPacket;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.loot.LootTables;

/**
 * @author firiz
 */
public class PlayerListener implements Listener {

    private final InventoryManager inventoryManager = AtelierPlugin.getPlugin().getInventoryManager();
    private static final ArrowManager arrowManager = ArrowManager.INSTANCE;

    @EventHandler
    private void readyArrow(final PlayerReadyArrowEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getBow();
        if (item.getType() == Material.CROSSBOW && !((CrossbowMeta) item.getItemMeta()).hasChargedProjectiles()) {
            e.setCancelled(arrowManager.interactCrossbow(player));
        }
    }

    @EventHandler
    private void interact(final PlayerInteractEvent e) {
        final Action action = e.getAction();
        final ItemStack item = e.getItem();
        final Block block = e.getClickedBlock();
        final Player player = e.getPlayer();

        final boolean hasBlock = block != null;
        if (hasBlock && block.getState() instanceof LootableBlockInventory) { // ブロック(チェスト・シェルカーボックスなど)でのアイテムルート時、アイテム更新とデバッグ
            final BlockState state = block.getState();
            final LootableBlockInventory loot = (LootableBlockInventory) state;
            if (player.isOp() && item != null && item.getType() == Material.STICK) { // デバッグ用
                e.setCancelled(true);
                loot.setLootTable(LootTables.ABANDONED_MINESHAFT.getLootTable(), Randomizer.nextLong());
                state.update();
                player.sendMessage("set lootTable " + loot.hasLootTable());
            } else if (loot.hasLootTable()) {
                ReplaceVanillaItems.loot(player, loot);
            }
        } else if (CommonUtils.isRight(action)) {
            if (hasBlock) {
                switch (block.getType()) {
                    case LECTERN:
                        if (QuestBook.lectern(player, block, item)) {
                            return;
                        }
                        break;
                    case CAULDRON:
                        final AlchemyInventoryType type = AlchemyInventoryType.search(action, item, block, player);
                        if (type != null) {
                            e.setCancelled(type.run(action, item, block, player));
                            inventoryManager.getInventory(RecipeSelectInventory.class).open(player, block.getLocation());
                            return;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (item != null) {
                if (item.hasItemMeta()) {
                    switch (item.getType()) {
                        case WRITTEN_BOOK:
                            if (QuestBook.getQuestBookMaterial(item) != null) {
                                e.setCancelled(true);
                                QuestBook.openQuestBook(player);
                                return;
                            }
                            break;
                        default:
                            // alchemyMaterial
                            break;
                    }
                }
            }
            // use_item -------
            if (ScriptItem.start(e)) {
                // なんか作る
            }
            //---
        }

    }

    @EventHandler
    private void pickup(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            inventoryManager.getInventory(AlchemyKettleInventory.class).pickup(e);
        }
    }

    @EventHandler
    private void interactEntity(final AsyncPlayerInteractEntityEvent e) {
        final Player player = e.getPlayer();
        final Entity rightClicked = e.getEntity();
        if (rightClicked instanceof LootableEntityInventory) { // エンティティ(チェストマインカートなど)でのアイテムルート時、アイテム更新とデバッグ
            final LootableEntityInventory loot = (LootableEntityInventory) rightClicked;
            if (player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.STICK) { // デバッグ用
                e.setCancelled(true);
                loot.setLootTable(LootTables.ABANDONED_MINESHAFT.getLootTable(), Randomizer.nextLong());
                player.sendMessage("set lootTable " + loot.hasLootTable());
            } else if (loot.hasLootTable()) {
                ReplaceVanillaItems.loot(player, loot);
            }
        } else if (e.getHand() == EquipmentSlot.HAND) {
            if (rightClicked != null) {
                e.setCancelled(NPCManager.INSTANCE.action(player, rightClicked));
            } else if (e.getEntityId() < 0) { // fakeEntity
                e.setCancelled(NPCManager.INSTANCE.action(player, e.getEntityId()));
            }
        }
    }

    @EventHandler
    private void join(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        PlayerSaveManager.INSTANCE.loadStatus(player);
        PlayerInjection.inject(player);
        NPCManager.INSTANCE.packet(player);
        Notification.loginNotification(player);
        AtelierPlugin.getPlugin().getTabList().update(player);
        PayloadPacket.sendBrand(player);
    }

    @EventHandler
    private void held(final PlayerItemHeldEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateWeapon(e.getNewSlot());
    }

    @EventHandler
    private void armorChange(final PlayerArmorChangeEvent e) {
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).getCharStats().updateEquip();
    }

    @EventHandler
    private void quit(final PlayerQuitEvent e) {
        PlayerSaveManager.INSTANCE.unloadStatus(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void changeWorld(final PlayerChangedWorldEvent e) {
        NPCManager.INSTANCE.packet(e.getPlayer());
    }

    @EventHandler
    private void respawn(final PlayerRespawnEvent e) {
        NPCManager.INSTANCE.packet(e.getPlayer());
        PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).respawn();
    }

    @EventHandler
    private void death(final PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }

    @EventHandler
    private void expChange(final PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler
    private void pickup(final PlayerAttemptPickupItemEvent e) {
        if (e.getItem().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            PlayerSaveManager.INSTANCE.getChar(e.getPlayer().getUniqueId()).increaseIdea(e.getItem().getItemStack());
        }
    }

}
