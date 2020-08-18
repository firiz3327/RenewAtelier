package net.firiz.renewatelier.listener;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import com.destroystokyo.paper.loottable.LootableEntityInventory;
import net.firiz.renewatelier.alchemy.kettle.inventory.RecipeSelectInventory;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.quest.book.QuestBook;
import net.firiz.renewatelier.script.ScriptItem;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.minecraft.ReplaceVanillaItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

class PlayerInteractManager {

    void interact(Cancellable e, Player player, Action action, Block block, ItemStack item) {
        if (block != null && block.getState() instanceof LootableBlockInventory) { // ブロック(チェスト・シェルカーボックスなど)でのアイテムルート時、アイテム更新とデバッグ
            final BlockState state = block.getState();
            final LootableBlockInventory loot = (LootableBlockInventory) state;
            if (player.isOp() && item != null && item.getType() == Material.STICK) { // デバッグ用
                e.setCancelled(true);
                if (player.isSneaking() && loot instanceof Container) {
                    ((Container) loot).getSnapshotInventory().setStorageContents(new ItemStack[0]);
                    player.sendMessage("clear container");
                } else {
                    loot.setLootTable(LootTables.NETHER_BRIDGE.getLootTable(), Randomizer.nextLong());
                    player.sendMessage("set lootTable " + loot.hasLootTable());
                }
                state.update();
            }
        } else if (CommonUtils.isRight(action)) {
            interactRight(e, player, action, block, item);
        }
    }

    private void interactRight(Cancellable e, Player player, Action action, Block block, ItemStack item) {
        if (!NPCManager.INSTANCE.canAction(player)) { // Player(NPC)を右クリックするとアイテムを使ってしまうのを防ぐ
            e.setCancelled(true);
            return;
        }

        if (block != null && interactBlock(e, player, action, block, item) == Result.RETURN) {
            return;
        }
        if (item != null && interactItem(e, player, item) == Result.RETURN) {
            return;
        }
        if (ScriptItem.start(e, player, item)) {
            // なんか作ろうとしてたような？忘れた
        }
    }

    private Result interactBlock(Cancellable e, Player player, Action action, Block block, ItemStack item) {
        switch (block.getType()) {
            case LECTERN:
                if (QuestBook.lectern(e, player, block, item)) {
                    return Result.RETURN;
                }
                break;
            case ENCHANTING_TABLE:
                e.setCancelled(true);
                // スキル管理GUIでも作ろうかな
                return Result.RETURN;
            case CAULDRON:
                final AlchemyInventoryType type = AlchemyInventoryType.search(action, item, block, player);
                if (type != null) {
                    e.setCancelled(type.run(action, item, block, player));
                    InventoryManager.INSTANCE.getInventory(RecipeSelectInventory.class).open(player, block.getLocation());
                    return Result.RETURN;
                }
                break;
            default:
                break;
        }
        return Result.NONE;
    }

    private Result interactItem(Cancellable e, Player player, ItemStack item) {
        if (item.getType() == GameConstants.USABLE_MATERIAL) {
            e.setCancelled(true);
        }
        if (item.hasItemMeta()) {
            if (!player.hasCooldown(GameConstants.USABLE_MATERIAL) && item.getType() == GameConstants.USABLE_MATERIAL && AlchemyItemStatus.has(item)) {
                final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
                assert itemStatus != null;
                if (itemStatus.getAlchemyMaterial().getItemSkill() != null) {
                    e.setCancelled(true);
                    if (itemStatus.canUse()) {
                        itemStatus.incrementConsumedCount();
                        itemStatus.updateItem(item);
                        itemStatus.getAlchemyMaterial().getItemSkill().createSkill(
                                player,
                                itemStatus
                        ).fire();
                    } else {
                        player.sendMessage("残り使用回数がありません");
                    }
                    return Result.RETURN;
                }
            } else if (item.getType() == Material.WRITTEN_BOOK && QuestBook.getQuestBookMaterial(item) != null) {
                e.setCancelled(true);
                QuestBook.openQuestBook(player);
                return Result.RETURN;
            }
        }
        return Result.NONE;
    }

    void interactEntity(Cancellable e, Player player, Entity entity, EquipmentSlot hand, boolean isRightClick, int entityId) {
        if (entity instanceof LootableEntityInventory) { // エンティティ(チェストマインカートなど)でのアイテムルート時、アイテム更新とデバッグ
            final LootableEntityInventory loot = (LootableEntityInventory) entity;
            if (player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.STICK) { // デバッグ用
                e.setCancelled(true);
                if (player.isSneaking() && loot instanceof InventoryHolder) {
                    ((InventoryHolder) loot).getInventory().clear();
                    player.sendMessage("clear container");
                } else {
                    loot.setLootTable(LootTables.ABANDONED_MINESHAFT.getLootTable(), Randomizer.nextLong());
                    player.sendMessage("set lootTable " + loot.hasLootTable());
                }
            }
        } else if (isRightClick && hand == EquipmentSlot.HAND) {
            if (entity != null) {
                e.setCancelled(NPCManager.INSTANCE.action(player, entity));
            } else if (entityId < 0) { // fakeEntity
                e.setCancelled(NPCManager.INSTANCE.action(player, entityId));
            }
        }
    }

    private enum Result {
        RETURN,
        NONE
    }
}
