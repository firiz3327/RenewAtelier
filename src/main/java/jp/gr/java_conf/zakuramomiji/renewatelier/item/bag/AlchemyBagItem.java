/*
 * AlchemyBag.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.item.bag;

import de.tr7zw.itemnbtapi.NBTItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterialManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author kanzakiayaka
 */
public class AlchemyBagItem {

    private final AlchemyMaterial type;
    private ItemStack item;

    public AlchemyBagItem(final AlchemyMaterial type) {
        this.type = type;
        this.item = Chore.createDamageableItem(type.getMaterial().getLeft(), 1, type.getMaterial().getRight());
        init(null);
    }

    public AlchemyBagItem(final AlchemyMaterial type, final List<String> lore) {
        this.type = type;
        this.item = Chore.createDamageableItem(type.getMaterial().getLeft(), 1, type.getMaterial().getRight());
        init(lore);
    }

    public ItemStack getItem() {
        return item;
    }

    private void init(final List<String> lore) {
        final ItemMeta meta = item.getItemMeta();

        if (!type.isDefaultName()) {
            meta.setDisplayName(type.getName());
        }
        final List<String> _lore;
        if (lore == null) {
            _lore = new ArrayList<>();
            _lore.add(AlchemyItemStatus.BAG.getCheck() + Chore.createStridColor(type.getId()));
        } else {
            _lore = lore;
        }
        meta.setLore(_lore);

        meta.setUnbreakable(type.isUnbreaking());
        if (type.isHideAttribute()) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        if (type.isHideDestroy()) {
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        }
        if (type.isHideEnchant()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (type.isHidePlacedOn()) {
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        }
        if (type.isHidePotionEffect()) {
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }
        if (type.isHideUnbreaking()) {
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        item.setItemMeta(meta);
    }

    public static ItemStack addItem(final ItemStack item, final AlchemyBagItem bag, final ItemStack _item) {
        final AlchemyMaterial material = AlchemyMaterialManager.getInstance().getMaterial(_item);
        if (material != null && material == bag.type) {
            final Object[] itemData = createItem(_item);
            final NBTItem nbti = new NBTItem(item);
            final List<String> items = nbti.hasKey("items") ? new ArrayList<>(Arrays.asList(nbti.getString("items").split("\n"))) : new ArrayList<>();

            System.out.println("additem");
            int amount = _item.getAmount(); // 64
            for (int i = 0; i < items.size(); i++) { // 0:16
                final String[] data = items.get(i).split(","); // amount, damage, lore
                if (amount > 0) {
                    if (Integer.parseInt(data[1]) == (int) itemData[1] && data[2].equals(itemData[2])) { // damageとloreが一致した時
                        int da = Integer.parseInt(data[0]) + amount; // 16 + 64 = 80
                        amount = da - 64;
                        data[0] = String.valueOf(Math.min(64, da));
                    }
                }
                items.set(i, parse(data));
            }
            if (amount > 0) {
                itemData[0] = amount;
                items.add(parse(itemData));
            }

            final StringBuilder sb = new StringBuilder();
            items.forEach((str) -> {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(str);
            });
            nbti.setString("items", sb.toString());
            return nbti.getItem();
        }
        return null;
    }

    private static String parse(final Object[] strs) {
        return strs[0] + "," + strs[1] + "," + strs[2];
    }

    private static Object[] createItem(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        final StringBuilder sb = new StringBuilder();
        item.getItemMeta().getLore().forEach((str) -> {
            if (sb.length() != 0) {
                sb.append("¥0");
            }
            sb.append(str);
        });
        return new Object[]{item.getAmount(), Chore.getDamage(meta), sb.toString()};
    }

    public static void openInventory(final Player player, final ItemStack item) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.BAG, item);
        if (!lores.isEmpty()) {
            final Inventory inv = Bukkit.createInventory(player, 54, "バッグ");
            final NBTItem nbti = new NBTItem(item);
            final List<String> items = nbti.hasKey("items") ? new ArrayList<>(Arrays.asList(nbti.getString("items").split("\n"))) : new ArrayList<>();
            int slot = 0;
            final String bagstr = lores.get(0).substring(AlchemyItemStatus.BAG.getCheck().length());
            final AlchemyMaterial type = AlchemyMaterialManager.getInstance().getMaterial(Chore.getStridColor(bagstr));
            for (final String datastr : items) {
                final String[] data = datastr.split(","); // amount, damage, lore
                final ItemStack _item = new ItemStack(
                        type.getMaterial().getLeft(),
                        Integer.parseInt(data[0]),
                        Short.parseShort(data[1])
                );
                final ItemMeta meta = _item.getItemMeta();
                meta.setLore(Arrays.asList(data[2].split("¥0")));

                meta.setUnbreakable(type.isUnbreaking());
                if (type.isHideAttribute()) {
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }
                if (type.isHideDestroy()) {
                    meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                }
                if (type.isHideEnchant()) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if (type.isHidePlacedOn()) {
                    meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                }
                if (type.isHidePotionEffect()) {
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }
                if (type.isHideUnbreaking()) {
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }
                _item.setItemMeta(meta);
                inv.setItem(slot, _item);
                slot++;
            }
            player.openInventory(inv);
        }
    }

    public static AlchemyBagItem getBag(final ItemStack item) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.BAG, item);
        if (!lores.isEmpty()) {
            final String data = lores.get(0).substring(AlchemyItemStatus.BAG.getCheck().length());
            return new AlchemyBagItem(
                    AlchemyMaterialManager.getInstance().getMaterial(Chore.getStridColor(data)),
                    item.getItemMeta().getLore()
            );
        }
        return null;
    }

}
