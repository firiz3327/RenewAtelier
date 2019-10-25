/*
 * RecipeSelect.java
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
package net.firiz.renewatelier.inventory.alchemykettle;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.player.PlayerSaveManager;
import net.firiz.renewatelier.player.Char;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import net.firiz.renewatelier.version.packet.InventoryPacket;
import net.firiz.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * @author firiz
 */
public final class RecipeSelect {

    private RecipeSelect() {
    }

    private static final String RECIPE_VALUE1 = "レシピを選択してください。";
    private static final List<String> RECIPE_LORES;
    private static final String STRING_MATERIAL = "material:";

    static {
        RECIPE_LORES = new ArrayList<>();
        RECIPE_LORES.add(ChatColor.RESET + RECIPE_VALUE1);
        RECIPE_LORES.add("");
    }

    public static boolean isKettleRecipe(final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_SELECT_RECIPE.getCheck());
    }

    public static void openGUI(final Player player, final Location loc) {
        final Inventory inv = Bukkit.createInventory(player, 54, AlchemyInventoryType.KETTLE_SELECT_RECIPE.getCheck());
        inv.setItem(0, Chore.ci(Material.DIAMOND_AXE, 1522, "", RECIPE_LORES));
        inv.setItem(45, Chore.ci(Material.DIAMOND_AXE, 1562, "", null));
        inv.setItem(2, Chore.ci(Material.BARRIER, 0, Chore.setLocXYZ(loc), RECIPE_LORES));

        setRecipeScroll(player.getUniqueId(), inv, 0);
        player.openInventory(inv);
        InventoryPacket.update(player, "", InventoryPacketType.CHEST);
    }

    private static void addRecipeStatus(final UUID uuid, final AlchemyRecipe recipe, final RecipeStatus rs, final List<String> lore) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        lore.add(Chore.createStridColor(recipe.getId()));
        lore.add(ChatColor.GRAY + "必要錬金レベル: " + (status.getCharStats().getAlchemyLevel() >= recipe.getReqAlchemyLevel() ? ChatColor.GREEN : "") + recipe.getReqAlchemyLevel());

        int addAmount = 0;
        final int level = rs.getLevel();
        if (level != 0) {
            lore.add(ChatColor.GRAY + "熟練度: ".concat(GameConstants.RANK_RECIPE[level]));
            final StringBuilder sb = new StringBuilder();
            if (level != 4) {
                int expPer = (int) (100 * ((double) rs.getExp() / GameConstants.RECIPE_REQ_EXPS[level]));
                for (int j = 0; j < 100; j++) {
                    sb.append(expPer > j ? ChatColor.GREEN : ChatColor.WHITE).append("|");
                }
            } else {
                sb.append(ChatColor.GREEN).append("||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            }
            lore.add(sb.toString());

            final List<RecipeLevelEffect> rles = recipe.getLevels().get(rs.getLevel());
            if (rles != null && !rles.isEmpty()) {
                for (final RecipeLevelEffect rle : rles) {
                    final RecipeLevelEffect.RecipeLEType type = rle.getType();
                    lore.add(ChatColor.GRAY + "- ".concat(type.getName()).concat(type.isViewNumber() ? " +".concat(String.valueOf(rle.getCount(type))) : ""));
                    if (type == RecipeLevelEffect.RecipeLEType.ADD_AMOUNT) {
                        addAmount += rle.getCount();
                    }
                }
            } else {
                lore.add(ChatColor.GRAY + "熟練度効果なし");
            }
        } else {
            lore.add(ChatColor.GRAY + "熟練度なし");
        }
        lore.add("");

        lore.add(ChatColor.GRAY + "作成量: " + (recipe.getAmount() + addAmount));
        lore.add(ChatColor.GRAY + "必要素材:");
        for (final String req : recipe.getReqMaterial()) {
            final String[] data = req.split(",");
            if (data[0].startsWith("category:")) {
                lore.add(AlchemyItemStatus.Type.CATEGORY.getCheck() + "§7- " + ChatColor.stripColor(Category.valueOf(data[0].substring(9)).getName()) + " × " + data[1]);
            } else if (data[0].startsWith(STRING_MATERIAL)) {
                lore.add(AlchemyItemStatus.Type.MATERIAL.getCheck() + "§7- " + ChatColor.stripColor(AlchemyMaterial.getMaterial(data[0].substring(9)).getName()) + " × " + data[1]);
            }
        }
    }

    private static void setRecipeScroll(final UUID uuid, final Inventory inv, final int scroll) {
        final List<DoubleData<RecipeStatus, DoubleData<Material, Integer>>> ritem = new ArrayList<>();
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        status.getRecipeStatusList().forEach(rs -> {
            final String result_str = AlchemyRecipe.search(rs.getId()).getResult();
            final String[] result = result_str.contains(",") ? result_str.split(",") : new String[]{result_str};
            DoubleData<Material, Integer> material = null;
            if (result[0].startsWith(STRING_MATERIAL)) {
                material = AlchemyMaterial.getMaterial(result[0].substring(9)).getMaterial();
            } else if (result[0].startsWith("minecraft:")) {
                material = new DoubleData<>(Material.getMaterial(result[0].substring(10)), result.length > 1 ? Integer.parseInt(result[1]) : 0);
            }
            if (material != null) {
                ritem.add(new DoubleData<>(rs, material));
            }
        });
        ritem.sort(Comparator.comparing((DoubleData<RecipeStatus, DoubleData<Material, Integer>> o) -> o.getLeft().getId()));
        final int dScroll = scroll * 6;
        if (ritem.size() > dScroll) {
            final ItemStack setting = Chore.ci(Material.BARRIER, 0, "", null);
            final ItemMeta meta = setting.getItemMeta();
            AlchemyChore.setSetting(meta, 0, scroll, RECIPE_VALUE1);
            AlchemyChore.setSetting(meta, 1, 0, ""); // 改行用
            setting.setItemMeta(meta);
            inv.setItem(1, setting);

            int slot = 9;
            deleteInv:
            for (int i = slot; i < slot + 24; i++) {
                inv.setItem(slot, null);
                switch (slot) {
                    case 14:
                    case 23:
                    case 32:
                        slot += 4;
                        break;
                    case 41:
                        break deleteInv;
                    default:
                        slot++;
                        break;
                }
            }

            slot = 9;
            for (int i = dScroll; i < dScroll + 24; i++) {
                if (ritem.size() <= i) {
                    break;
                }
                final DoubleData<RecipeStatus, DoubleData<Material, Integer>> dd = ritem.get(i);
                final DoubleData<Material, Integer> material = dd.getRight();
                final RecipeStatus rs = dd.getLeft();
                final AlchemyRecipe recipe = AlchemyRecipe.search(rs.getId());
                final ItemStack item;
                final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());

                item = recipeStatus.getLevel() == 0 ? new ItemStack(Material.FILLED_MAP) : Chore.createCustomModelItem(material.getLeft(), 1, material.getRight());
                final ItemMeta iMeta = item.getItemMeta();
                final AlchemyMaterial am = AlchemyMaterial.getMaterial((recipe.getResult().contains(",") ? recipe.getResult().split(",")[0] : recipe.getResult()).substring(9));
                setMetaDatas(iMeta, am);

                final List<String> lore = new ArrayList<>();
                addRecipeStatus(uuid, recipe, rs, lore);
                lore.add("");
                iMeta.setLore(lore);

                item.setItemMeta(iMeta);
                inv.setItem(slot, item);
                switch (slot) {
                    case 14:
                    case 23:
                    case 32:
                        slot += 4;
                        break;
                    case 41:
                        return;
                    default:
                        slot++;
                        break;
                }
            }
        }
    }

    private static void setMetaDatas(final ItemMeta meta, final AlchemyMaterial am) {
        if (am != null) {
            if (!am.isDefaultName()) {
                meta.setDisplayName(am.getName());
            }
            Chore.addHideFlags(meta, am);
        }
    }

    public static void click(final InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.isShiftClick()) { // 増殖防止 || アイテム混入防止
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        if (raw >= 0 && raw < inv.getSize()) {
            e.setCancelled(true);
        }
        if (e.getSlotType() == SlotType.CONTAINER && inv.getItem(1) != null) {
            final Player player = (Player) e.getWhoClicked();
            final int scroll = AlchemyChore.getSetting(inv.getItem(1).getItemMeta(), 0);
            final ItemStack item = e.getCurrentItem();

            switch (raw) {
                case 46:
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    setRecipeScroll(player.getUniqueId(), inv, Math.max(0, scroll - 1));
                    break;
                case 49:
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    setRecipeScroll(player.getUniqueId(), inv, scroll + 1);
                    break;
                case 25:
                    if (item != null) {
                        final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(item.getItemMeta().getLore().get(1)));
                        if (Chore.hasMaterial(player.getInventory(), recipe.getReqMaterial())) {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                            ItemSelect.openItemSelect(player, recipe, inv);
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                    }
                    break;
                default:
                    if (raw >= 9 && raw <= 14
                            || raw >= 18 && raw <= 23
                            || raw >= 27 && raw <= 32
                            || raw >= 36 && raw <= 41) {
                        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                            final AlchemyRecipe recipe = AlchemyRecipe.search(Chore.getStridColor(item.getItemMeta().getLore().get(0)));
                            final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
                            if (!Chore.hasMaterial(player.getInventory(), recipe.getReqMaterial())) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                break;
                            } else {
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                            }
                            final String[] result = recipe.getResult().contains(",") ? recipe.getResult().split(",") : new String[]{recipe.getResult()};
                            AlchemyMaterial am = null;
                            DoubleData<Material, Integer> material = null;
                            if (result[0].startsWith(STRING_MATERIAL)) {
                                am = AlchemyMaterial.getMaterial(result[0].substring(9));
                                material = am.getMaterial();
                            } else if (result[0].startsWith("minecraft:")) {
                                material = new DoubleData<>(Material.getMaterial(result[0].substring(10)), result.length > 1 ? Integer.parseInt(result[1]) : 0);
                            }

                            final List<String> lore = new ArrayList<>();
                            lore.add(ChatColor.WHITE + "  を作成します。");
                            final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());
                            if (recipeStatus != null && material != null) {
                                RecipeSelect.addRecipeStatus(player.getUniqueId(), recipe, recipeStatus, lore);
                                final ItemStack resultItem = recipeStatus.getLevel() == 0 ? new ItemStack(Material.FILLED_MAP, recipe.getAmount()) : Chore.createCustomModelItem(material.getLeft(), recipe.getAmount(), material.getRight());
                                final ItemMeta meta = resultItem.getItemMeta();
                                setMetaDatas(meta, am);
                                meta.setLore(lore);
                                resultItem.setItemMeta(meta);
                                inv.setItem(25, resultItem);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        }
                    }
                    break;
            }
        }
    }

    public static void drag(InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }
}
