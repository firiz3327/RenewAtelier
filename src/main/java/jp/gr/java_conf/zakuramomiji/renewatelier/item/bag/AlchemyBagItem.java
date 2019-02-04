/*
 * AlchemyBagItem.java
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
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.InventoryPacket;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class AlchemyBagItem {

    private final static List<UUID> OPEN_USERS = new ArrayList<>();
    private final AlchemyMaterial type;
    private final ItemStack item;

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

    public AlchemyMaterial getType() {
        return type;
    }

    public static DoubleData<ItemStack, ItemStack> addItem(final ItemStack bag_item, final ItemStack add_item) {
        final Object[] itemData = createItem(add_item);
        final NBTItem nbti = new NBTItem(bag_item);
        final List<String> items = nbti.hasKey("items") ? new ArrayList<>(Arrays.asList(nbti.getString("items").split("\n"))) : new ArrayList<>();

        int amount = add_item.getAmount(); // 64
        for (int i = 0; i < items.size(); i++) { // 0:16
            final String[] data = items.get(i).split(","); // amount, damage, lore, (name) -> isDisplayName(true)
            if (amount > 0) {
                if (data[1].equals(itemData[1]) && data[2].equals(itemData[2])
                        && (data.length == 4 ? (itemData.length == 4 ? data[3].equals(itemData[3]) : false) : itemData.length != 4)) { // damageとloreとnameが一致した時
                    final int da = Integer.parseInt(data[0]) + amount; // 16 + 64 = 80
                    amount = da - 64;
                    data[0] = String.valueOf(Math.min(64, da));
                }
            }
            items.set(i, parse(data));
        }
        ItemStack add = null;
        if (amount > 0) {
            if (items.size() < 36) {
                itemData[0] = amount;
                items.add(parse(itemData));
            } else {
                add = add_item.clone();
                add.setAmount(amount);
            }
        }
        final StringBuilder sb = new StringBuilder();
        items.forEach((str) -> {
            if (sb.length() != 0) {
                sb.append("\n");
            }
            sb.append(str);
        });
        nbti.setString("items", sb.toString());
        return new DoubleData<>(nbti.getItem(), add);
    }

    public static ItemStack removeItem(final ItemStack bag_item, final ItemStack remove_item) {
        final Object[] itemData = createItem(remove_item);
        final NBTItem nbti = new NBTItem(bag_item);
        final List<String> items = nbti.hasKey("items") ? new ArrayList<>(Arrays.asList(nbti.getString("items").split("\n"))) : new ArrayList<>();

        int amount = remove_item.getAmount();
        if (items.size() == 1) {
            final String[] data = items.get(0).split(","); // amount, damage, lore
            final int da = Integer.parseInt(data[0]) - amount; // 16 - 64 = -48
            final int setamount = Math.max(0, da);
            if (setamount != 0) {
                data[0] = String.valueOf(setamount);
                items.set(0, parse(data));
            } else {
                return null;
            }
        } else {
            for (int i = items.size() - 1; i >= 0; i--) {
                final String[] data = items.get(i).split(","); // amount, damage, lore
                if (amount > 0) {
                    if (data[1].equals(itemData[1]) && data[2].equals(itemData[2])
                            && data.length == 4 ? (itemData.length == 4 ? data[3].equals(itemData[3]) : false) : itemData.length != 4) { // damageとloreとnameが一致した時
                        final int da = Integer.parseInt(data[0]) - amount; // 16 - 64 = -48
                        amount = da * -1;

                        final int setamount = Math.max(0, da);
                        if (setamount != 0) {
                            data[0] = String.valueOf(setamount);
                        } else {
                            items.remove(i);
                            continue;
                        }
                    }
                }
                items.set(i, parse(data));
            }
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

    private static String parse(final Object[] strs) {
        return strs[0] + "," + strs[1] + "," + strs[2] + (strs.length == 4 ? ("," + strs[3]) : "");
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
        return meta.hasDisplayName()
                ? new Object[]{item.getAmount(), item.getType().toString() + ":" + Chore.getDamage(meta), sb.toString(), meta.getDisplayName().replace(",", "¥.¥")}
                : new Object[]{item.getAmount(), Chore.getDamage(meta), sb.toString()};
    }

    public static boolean isBagInventory(final InventoryView view) {
        return view.getTitle().startsWith("AlchemyBag,");
    }

    public static void openInventory(final Player player, final ItemStack item, final int clickslot) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.BAG, item);
        if (!lores.isEmpty()) {
            final Inventory inv = Bukkit.createInventory(player, 45, "AlchemyBag," + clickslot);
            final NBTItem nbti = new NBTItem(item);
            final List<String> items = nbti.hasKey("items") ? new ArrayList<>(Arrays.asList(nbti.getString("items").split("\n"))) : new ArrayList<>();
            int slot = 0;
            final String bagstr = lores.get(0).substring(AlchemyItemStatus.BAG.getCheck().length());
            final AlchemyMaterial type = AlchemyMaterial.getMaterial(Chore.getStridColor(bagstr));
            for (final String datastr : items) {
                final String[] data = datastr.split(","); // amount, damage, lore
                final String[] idData = data[1].split(":");
                final ItemStack _item = Chore.createDamageableItem(
                        Material.valueOf(idData[0]),
                        Integer.parseInt(data[0]),
                        Integer.parseInt(idData[1])
                );
                final ItemMeta meta = _item.getItemMeta();
                if (data.length == 4) {
                    meta.setDisplayName(data[3].replace("¥.¥", ","));
                }
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
            OPEN_USERS.add(player.getUniqueId());
            InventoryPacket.update(player, "アイテム", InventoryPacketType.CHEST);
        }
    }

    public static AlchemyBagItem getBag(final ItemStack item) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.BAG, item);
        if (!lores.isEmpty()) {
            final String data = lores.get(0).substring(AlchemyItemStatus.BAG.getCheck().length());
            return new AlchemyBagItem(
                    AlchemyMaterial.getMaterial(Chore.getStridColor(data)),
                    item.getItemMeta().getLore()
            );
        }
        return null;
    }

    public static void click(final InventoryClickEvent e) {
        e.setCancelled(true);
        final Player player = (Player) e.getWhoClicked();
        final PlayerInventory pinv = player.getInventory();
        final ItemStack currentItem = e.getCurrentItem();
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            final int bag_slot = Integer.parseInt(e.getView().getTitle().split(",")[1]);
            if (e.getSlotType() == SlotType.CONTAINER) {
                final ItemStack removeItem = currentItem.clone();
                if (e.getClick() == ClickType.RIGHT) {
                    removeItem.setAmount(1);
                }
                final ItemStack bag_item = pinv.getItem(bag_slot);
                final AlchemyBagItem bag = getBag(bag_item);
                pinv.setItem(bag_slot, removeItem(bag_item, removeItem));

                ItemStack next_bag_item = pinv.getItem(bag_slot);
                if (pinv.getItem(bag_slot) != null) {
                    final ItemStack rest = Chore.addItemNotDrop(pinv, removeItem);
                    if (rest != null) {
                        next_bag_item = addItem(next_bag_item, rest).getLeft();
                        pinv.setItem(bag_slot, next_bag_item);
                    }

                    // reflesh
                    openInventory(player, next_bag_item, bag_slot);
                } else {
                    Chore.addItem(player, removeItem);
                    player.closeInventory();
                }
            } else if (e.getSlot() != bag_slot) {
                final AlchemyMaterial material = AlchemyMaterial.getMaterial(currentItem);
                final ItemStack bag_item = pinv.getItem(bag_slot);
                final AlchemyBagItem bag = getBag(bag_item);
                if (material != null && bag.getType() == material) {
                    final ItemStack item = currentItem.clone();
                    if (e.getClick() == ClickType.RIGHT) {
                        item.setAmount(1);
                    }
                    currentItem.setAmount(currentItem.getAmount() - item.getAmount());
                    final DoubleData<ItemStack, ItemStack> next_bag_item = addItem(bag_item, item);
                    pinv.setItem(bag_slot, next_bag_item.getLeft());
                    if (next_bag_item.getRight() != null) {
                        Chore.addItem(player, next_bag_item.getRight());
                    }

                    // reflesh
                    openInventory(player, next_bag_item.getLeft(), bag_slot);
                }
            }
        }
    }

    public static void drag(final InventoryDragEvent e) {
        e.setCancelled(true);
    }

    public static void close(final InventoryCloseEvent e) {
        OPEN_USERS.remove(e.getPlayer().getUniqueId());
    }

    public static void pickup(final EntityPickupItemEvent e) {
        final Player player = (Player) e.getEntity();
        if (OPEN_USERS.contains(player.getUniqueId())) {
            e.setCancelled(true);
        }
        if(!e.isCancelled()) {
            final ItemStack dropitem = e.getItem().getItemStack();
            final AlchemyMaterial material = AlchemyMaterial.getMaterial(dropitem);
            if (material != null) {
                final PlayerInventory inv = player.getInventory();
                final ItemStack[] contents = inv.getContents();
                ItemStack check = dropitem;
                for (int i = 0; i < contents.length; i++) {
                    final ItemStack item = contents[i];
                    final AlchemyBagItem bag = getBag(item);
                    if (bag != null && bag.getType() == material) {
                        final DoubleData<ItemStack, ItemStack> bagItem = addItem(item, check);
                        contents[i] = bagItem.getLeft();
                        inv.setContents(contents);
                        check = bagItem.getRight();
                        if (check != null) {
                            continue;
                        }
                        break;
                    }
                }
                if (check != null) {
                    final AlchemyBagItem bag = new AlchemyBagItem(material);
                    final ItemStack item = bag.getItem();
                    Chore.addItem(player, addItem(item, check).getLeft());
                }
                e.getItem().getWorld().playSound(e.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }

}
