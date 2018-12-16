/*
 * KettleItemManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonus;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.box.KettleBox;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public enum KettleItemManager {
    INSTANCE; // enum singleton style
    
    private final Map<UUID, Map<Integer, List<ItemStack>>> use_items = new HashMap<>();
    private final Map<UUID, ItemStack> use_catalyst = new HashMap<>();
    private final Map<UUID, ItemStack[]> default_contents = new HashMap<>();
    private final Map<UUID, List<CatalystBonus>> catalyst_bonus = new HashMap<>();
    private final Map<UUID, KettleBox> kettleData = new HashMap<>();
    private final Map<UUID, List<Characteristic>> characteristics = new HashMap<>();
    private final Map<UUID, List<Characteristic>> select_characteristics = new HashMap<>();
    private final Map<UUID, List<Characteristic>> catalyst_characteristics = new HashMap<>();

    public void reset(final Player player) {
        final UUID uuid = player.getUniqueId();
        KettleBonusManager.INSTANCE.removeData(uuid);
        if (catalyst_bonus.containsKey(uuid)) {
            catalyst_bonus.remove(uuid);
        }
        if (kettleData.containsKey(uuid)) {
            kettleData.remove(uuid);
        }
        if (default_contents.containsKey(uuid)) {
            player.getInventory().setContents(default_contents.get(uuid));
            default_contents.remove(uuid);
        }
        if (use_items.containsKey(uuid)) {
            use_items.remove(uuid);
        }
        if (use_catalyst.containsKey(uuid)) {
            use_catalyst.remove(uuid);
        }
        if (characteristics.containsKey(uuid)) {
            characteristics.remove(uuid);
        }
        if (select_characteristics.containsKey(uuid)) {
            select_characteristics.remove(uuid);
        }
        if (catalyst_characteristics.containsKey(uuid)) {
            catalyst_characteristics.remove(uuid);
        }
    }

    public void allBack(final Player player) {
        final UUID uuid = player.getUniqueId();
        KettleBonusManager.INSTANCE.removeData(uuid);
        if (catalyst_bonus.containsKey(uuid)) {
            catalyst_bonus.remove(uuid);
        }
        if (kettleData.containsKey(uuid)) {
            kettleData.remove(uuid);
        }

        if (default_contents.containsKey(uuid)) {
            player.getInventory().setContents(default_contents.get(uuid));
            default_contents.remove(uuid);
        }
        if (use_items.containsKey(uuid)) {
            final Map<Integer, List<ItemStack>> use_item = use_items.get(uuid);
            use_item.values().forEach((items) -> {
                items.forEach((item) -> {
                    Chore.addItem(player, item);
                });
            });
            use_items.remove(uuid);
        }
        if (use_catalyst.containsKey(uuid)) {
            final ItemStack catalyst = use_catalyst.get(uuid);
            Chore.addItem(player, catalyst);
            use_catalyst.remove(uuid);
        }
        if (characteristics.containsKey(uuid)) {
            characteristics.remove(uuid);
        }
        if (select_characteristics.containsKey(uuid)) {
            select_characteristics.remove(uuid);
        }
    }

    /* アイテム選択画面 Start */
    public void addPageItem(final UUID uuid, final ItemStack item, final int page) {
        if (use_items.containsKey(uuid)) { // 追加処理
            final Map<Integer, List<ItemStack>> uses = use_items.get(uuid);
            if (uses.containsKey(page)) { // ページが存在する場合
                uses.get(page).add(item);
            } else { // ページが存在しない場合
                final List<ItemStack> array = new ArrayList<>();
                array.add(item);
                uses.put(page, array);
            }
        } else { // 初回追加処理
            final HashMap<Integer, List<ItemStack>> map = new HashMap<>();
            final List<ItemStack> array = new ArrayList<>();
            array.add(item);
            map.put(page, array);
            use_items.put(uuid, map);
        }
    }

    public void removePageItem(final UUID uuid, final int slot, final int page) {
        if (use_items.containsKey(uuid) && use_items.get(uuid).containsKey(page)) {
            use_items.get(uuid).get(page).remove(slot);
        }
    }

    public List<ItemStack> getPageItems(final UUID uuid, final int page) {
        if (use_items.containsKey(uuid)) {
            return use_items.get(uuid).get(page);
        }
        return null;
    }

    public Map<Integer, List<ItemStack>> getPageItems(final UUID uuid) {
        return use_items.get(uuid);
    }

    /* アイテム選択画面 End */
    public void setCatalyst(final UUID uuid, final ItemStack item) {
        use_catalyst.put(uuid, item);
    }

    public ItemStack getCatalyst(final UUID uuid) {
        if (use_catalyst.containsKey(uuid)) {
            return use_catalyst.get(uuid);
        }
        return null;
    }

    public void removeCatalyst(final UUID uuid) {
        use_catalyst.remove(uuid);
    }

    /* 錬金画面 Start */
    public boolean isOpenKettle(final UUID uuid) {
        return default_contents.containsKey(uuid);
    }
    
    public void setDefaultContents(final UUID uuid, final ItemStack[] contents) {
        default_contents.put(uuid, contents);
    }

    public List<CatalystBonus> getCatalystBonusList(final UUID uuid) {
        return catalyst_bonus.get(uuid);
    }

    public boolean hasCatalystBonus(final UUID uuid, final CatalystBonus bonus) {
        return catalyst_bonus.containsKey(uuid) ? catalyst_bonus.get(uuid).contains(bonus) : false;
    }

    public void addCatalystBonus(final UUID uuid, final CatalystBonus data) {
        if (catalyst_bonus.containsKey(uuid)) {
            catalyst_bonus.get(uuid).add(data);
            return;
        }
        final List<CatalystBonus> list = new ArrayList<>() {
            @Override
            public boolean contains(Object obj) {
                if (!(obj instanceof CatalystBonus)) {
                    Chore.log("not contains : " + obj);
                }
                return super.contains(obj);
            }
        };
        list.add(data);
        catalyst_bonus.put(uuid, list);
    }

    public boolean removeCatalystBonus(final UUID uuid, final CatalystBonus data) {
        if (catalyst_bonus.containsKey(uuid)) {
            return catalyst_bonus.get(uuid).remove(data);
        }
        return false;
    }

    public void addKettleData(final UUID uuid, final ItemStack item, final int csize, final Map<Integer, Integer> rslots) {
        if (!kettleData.containsKey(uuid)) {
            kettleData.put(uuid, new KettleBox(csize));
        }
        kettleData.get(uuid).addItem(item, rslots);
    }

    public KettleBox getKettleData(final UUID uuid) {
        return kettleData.get(uuid);
    }

    public List<Characteristic> getCharacteristics(final UUID uuid) {
        final List<Characteristic> cs = characteristics.get(uuid);
        if (cs == null) {
            return null;
        }
        final List<Characteristic> result = new ArrayList<>();
        cs.stream().filter((c) -> (!result.contains(c))).forEachOrdered((c) -> {
            result.add(c);
        });
        final List<Characteristic> ccs = catalyst_characteristics.get(uuid);
        if (ccs != null) {
            ccs.stream().filter((c) -> (!result.contains(c))).forEachOrdered((c) -> {
                result.add(c);
            });
        }
        return result;
    }

    public void addCharacteristic(final UUID uuid, final Characteristic characteristic, final boolean catalyst) {
        if (!catalyst) {
            if (!characteristics.containsKey(uuid)) {
                characteristics.put(uuid, new ArrayList<>());
            }
            characteristics.get(uuid).add(characteristic);
        } else {
            if (!catalyst_characteristics.containsKey(uuid)) {
                catalyst_characteristics.put(uuid, new ArrayList<>());
            }
            final List<Characteristic> cs = catalyst_characteristics.get(uuid);
            if(!cs.contains(characteristic)) {
                cs.add(characteristic);
            }
        }
    }

    public void removeCatalystCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (catalyst_characteristics.containsKey(uuid)) {
            catalyst_characteristics.get(uuid).remove(characteristic);
        }
    }

    public List<Characteristic> getSelectCharacteristics(final UUID uuid) {
        return select_characteristics.get(uuid);
    }

    public void addSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (!select_characteristics.containsKey(uuid)) {
            select_characteristics.put(uuid, new ArrayList<>());
        }
        final List<Characteristic> cs = select_characteristics.get(uuid);
        if (!cs.contains(characteristic)) {
            cs.add(characteristic);
        }
    }

    public void removeSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (select_characteristics.containsKey(uuid)) {
            select_characteristics.get(uuid).remove(characteristic);
        }
    }

    public boolean isSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (select_characteristics.containsKey(uuid)) {
            return select_characteristics.get(uuid).contains(characteristic);
        }
        return false;
    }

    public void resetSelectCharacteristic(final UUID uuid) {
        select_characteristics.remove(uuid);
    }
    /* 錬金画面 End */

}
