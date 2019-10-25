/*
 * ScriptConversation.java
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
package net.firiz.renewatelier.script.conversation;

import java.util.List;
import java.util.UUID;
import javax.script.Invocable;
import javax.script.ScriptException;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.inventory.ConfirmInventory;
import net.firiz.renewatelier.inventory.shop.ShopInventory;
import net.firiz.renewatelier.inventory.shop.ShopItem;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.player.PlayerSaveManager;
import net.firiz.renewatelier.player.Char;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.world.MyRoomManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.HostAccess.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public class ScriptConversation {

    protected final String scriptName;
    protected final Player player;
    protected Invocable iv;

    public ScriptConversation(String scriptName, Player player) {
        this.scriptName = scriptName;
        this.player = player;
    }

    @Export
    public void log(final Object str) {
        Chore.log(str);
    }

    @Export
    public void debug(final Object str) {
        Chore.log(str);
        player.sendMessage(str.toString());
    }

    @Export
    public void setIv(final Invocable iv) {
        this.iv = iv;
    }

    @Export
    public Player getPlayer() {
        return player;
    }

    @Export
    public Player getPlayer(final UUID uuid) {
        return AtelierPlugin.getPlugin().getServer().getPlayer(uuid);
    }

    @Export
    public Char getStatus() {
        return getStatus(player.getUniqueId());
    }

    @Export
    public Char getStatus(final UUID uuid) {
        return PlayerSaveManager.INSTANCE.getChar(uuid);
    }

    @Export
    public QuestStatus getQuestStatus(final String questId) {
        return getQuestStatus(player.getUniqueId(), questId);
    }

    @Export
    public QuestStatus getQuestStatus(final UUID uuid, final String questId) {
        return getStatus(uuid).getQuestStatus(questId);
    }

    @Export
    public boolean hasQuest(final String questId) {
        return hasQuestUUID(player.getUniqueId(), questId);
    }

    @Export
    public boolean hasQuestUUID(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId) != null;
    }

    @Export
    public boolean isQuestClear(final String questId) {
        return isQuestClear(player.getUniqueId(), questId);
    }

    @Export
    public boolean isQuestClear(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId).isClear();
    }

    @Export
    public void questStart(final String questId) {
        questStart(questId, true);
    }

    @Export
    public void questStart(final String questId, final boolean view) {
        if (view) {
            getStatus().addQuest(player, questId);
        } else {
            getStatus().addQuest(new QuestStatus(questId));
        }
    }

    @Export
    public void questClear(final String questId) {
        questClear(questId, true);
    }

    @Export
    public void questClear(final String questId, final boolean view) {
        getStatus().questClear(player, questId, view);
    }

    @Export
    public String getQuestName(final String questId) {
        return Quest.getQuest(questId).getName();
    }

    @Export
    public UUID getUUID(final String uuid) {
        return UUID.fromString(uuid);
    }

    @Export
    public String createStridColor(final String str) {
        return Chore.createStridColor(str);
    }

    @Export
    public String getStridColor(final String str) {
        return Chore.getStridColor(str);
    }

    @Export
    public String chatColor(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Export
    public List<String> getLores(final String check, final ItemStack item) {
        return AlchemyItemStatus.getLores(check, item);
    }

    @Export
    public void warpRoom(final Player player) {
        MyRoomManager.INSTANCE.warpRoom(player);
    }

    @Export
    public void warpRoom(final Player player, final UUID uuid) {
        MyRoomManager.INSTANCE.warpRoom(player, uuid);
    }

    @Export
    public boolean hasRoom(final UUID uuid) {
        return MyRoomManager.INSTANCE.hasRoom(uuid);
    }

    @Export
    public void createRoom(final UUID uuid) {
        MyRoomManager.INSTANCE.createRoom(uuid);
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName) {
        openConfirmInventory(title, yes, no, confirmFunctionName, cancelFunctionName, null);
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName, final String closeFunctionName) {
        ConfirmInventory.openInventory(
                player,
                ChatColor.translateAlternateColorCodes('&', title),
                yes,
                no,
                (final Player player1, final int select) -> {
                    final String functionName = select == 1 ? confirmFunctionName : select == 0 ? cancelFunctionName : closeFunctionName;
                    if (functionName != null) {
                        try {
                            iv.invokeFunction(functionName);
                        } catch (ScriptException ex) {
                            Chore.logWarning(ex);
                        } catch (NoSuchMethodException ignored) {
                        }
                    }
                }
        );
    }

    @Export
    public ShopItem shopItem(final ItemStack item, final int amount, final int price) {
        return new ShopItem(item, amount, price, null);
    }

    @Export
    public ShopItem shopItem(final ItemStack item, final int amount, final int price, final AlchemyMaterial coinType) {
        return new ShopItem(item, amount, price, coinType);
    }

    @Export
    public void openShopInventory(final String title, List<ShopItem> shopItems) {
        if (shopItems != null && shopItems.size() > 28) {
            throw new IllegalArgumentException("No more than 29 shop items can be placed.");
        }
        ShopInventory.openInventory(player, title, shopItems);
    }

    @NotNull
    @Export
    public AlchemyMaterial getAlchemyMaterial(final String material_id) {
        return AlchemyMaterial.getMaterial(material_id);
    }

    @NotNull
    @Export
    public AlchemyMaterial getAlchemyMaterial(final ItemStack item) {
        return AlchemyMaterial.getMaterial(item);
    }

    @Nullable
    @Export
    public AlchemyMaterial getAlchemyMaterialOrNull(final String material_id) {
        return AlchemyMaterial.getMaterialOrNull(material_id);
    }

    @Nullable
    @Export
    public AlchemyMaterial getAlchemyMaterialOrNull(final ItemStack item) {
        return AlchemyMaterial.getMaterialOrNull(item);
    }

    @Export
    public ItemStack itemStack(final Material material) {
        return itemStack(material, 1);
    }

    @Export
    public ItemStack itemStack(final Material material, int amount) {
        return new ItemStack(material, amount);
    }

    @Export
    public Material getMaterial(final String id) {
        return Chore.getMaterial(id);
    }

    @Export
    public EntityType getEntityType(final String entityType) {
        return EntityType.valueOf(entityType.toUpperCase());
    }

    @Export
    public AlchemyIngredients getIngredients(final String id) {
        return AlchemyIngredients.valueOf(id);
    }

    @Export
    public AlchemyIngredients getIngredientsForName(final String name) {
        return AlchemyIngredients.searchName(name);
    }

    @Export
    public int[] getSize(final String id, final int rotate) {
        return MaterialSize.valueOf(id).getSize(rotate);
    }

    @Export
    public Characteristic getCharacteristic(final String id) {
        return Characteristic.getCharacteristic(id);
    }

    @Export
    public Characteristic getCharacteristicForName(final String name) {
        return Characteristic.search(name);
    }

    @Export
    public ItemStack alchemyMaterial(final AlchemyMaterial material) {
        return alchemyMaterial(material, 1);
    }

    @Export
    public ItemStack alchemyMaterial(final AlchemyMaterial material, int amount) {
        final ItemStack item = AlchemyItemStatus.getItem(material);
        item.setAmount(amount);
        return item;
    }

    @Export
    public ItemStack alchemyMaterial(
            final AlchemyMaterial material,
            final int over_quality,
            final List<AlchemyIngredients> overIngs,
            int[] overSize,
            final List<String> activeEffects,
            final List<Characteristic> overCharacteristics,
            final List<Category> overCategory,
            final boolean notVisibleCatalyst
    ) {
        return AlchemyItemStatus.getItem(
                material,
                overIngs,
                null,
                over_quality,
                overSize,
                activeEffects,
                overCharacteristics,
                overCategory,
                notVisibleCatalyst
        );
    }

    @Export
    public void applyAlchemyMaterial(
            final ItemStack item,
            final AlchemyMaterial material,
            final List<AlchemyIngredients> overIngs,
            final int overQuality,
            final int[] overSize,
            final List<String> activeEffects,
            final List<Characteristic> overCharacteristics,
            final List<Category> overCategory,
            final boolean not_visible_catalyst
    ) {
        AlchemyItemStatus.getItem(
                material,
                overIngs,
                item,
                overQuality,
                overSize,
                activeEffects,
                overCharacteristics,
                overCategory,
                not_visible_catalyst
        );
    }

}
