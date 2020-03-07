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
package net.firiz.renewatelier.alchemy.kettle;

import java.util.*;

import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.kettle.bonus.KettleBonusManager;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
public enum KettleItemManager {
    INSTANCE; // enum singleton style
    
    private final Map<UUID, Map<Integer, List<ItemStack>>> useItems = new HashMap<>();
    private final Map<UUID, ItemStack> useCatalyst = new HashMap<>();
    private final Map<UUID, ItemStack[]> defaultContents = new HashMap<>();
    private final Map<UUID, List<CatalystBonus>> catalystBonus = new HashMap<>();
    private final Map<UUID, KettleBox> kettleData = new HashMap<>();
    private final Map<UUID, List<Characteristic>> characteristics = new HashMap<>();
    private final Map<UUID, List<Characteristic>> selectCharacteristics = new HashMap<>();
    private final Map<UUID, List<Characteristic>> catalystCharacteristics = new HashMap<>();

    private void clear(final Player player) {
        final UUID uuid = player.getUniqueId();
        KettleBonusManager.INSTANCE.removeData(uuid);
        catalystBonus.remove(uuid);
        kettleData.remove(uuid);
        if (defaultContents.containsKey(uuid)) {
            player.getInventory().setContents(defaultContents.get(uuid));
            defaultContents.remove(uuid);
        }
    }

    public void reset(final Player player) {
        final UUID uuid = player.getUniqueId();
        clear(player);
        useItems.remove(uuid);
        useCatalyst.remove(uuid);
        characteristics.remove(uuid);
        selectCharacteristics.remove(uuid);
        catalystCharacteristics.remove(uuid);
    }

    public void allBack(final Player player) {
        final UUID uuid = player.getUniqueId();
        clear(player);
        if (useItems.containsKey(uuid)) {
            final Map<Integer, List<ItemStack>> useItem = useItems.get(uuid);
            useItem.values().stream().flatMap(Collection::stream).forEach(item -> Chore.addItem(player, item));
            useItems.remove(uuid);
        }
        if (useCatalyst.containsKey(uuid)) {
            final ItemStack catalyst = useCatalyst.get(uuid);
            Chore.addItem(player, catalyst);
            useCatalyst.remove(uuid);
        }
        characteristics.remove(uuid);
        selectCharacteristics.remove(uuid);
    }


    //<editor-fold desc="アイテム選択画面">
    public void addPageItem(final UUID uuid, final ItemStack item, final int page) {
        if (useItems.containsKey(uuid)) { // 追加処理
            final Map<Integer, List<ItemStack>> uses = useItems.get(uuid);
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
            useItems.put(uuid, map);
        }
    }

    public void removePageItem(final UUID uuid, final int slot, final int page) {
        if (useItems.containsKey(uuid) && useItems.get(uuid).containsKey(page)) {
            useItems.get(uuid).get(page).remove(slot);
        }
    }

    @NotNull
    public List<ItemStack> getPageItems(final UUID uuid, final int page) {
        if (useItems.containsKey(uuid)) {
            return Objects.requireNonNullElse(useItems.get(uuid).get(page), new ArrayList<>(0));
        }
        return new ArrayList<>(0);
    }
    //</editor-fold>

    public void setCatalyst(final UUID uuid, final ItemStack item) {
        useCatalyst.put(uuid, item);
    }

    public ItemStack getCatalyst(final UUID uuid) {
        if (useCatalyst.containsKey(uuid)) {
            return useCatalyst.get(uuid);
        }
        return null;
    }

    public void removeCatalyst(final UUID uuid) {
        useCatalyst.remove(uuid);
    }


    //<editor-fold desc="錬金画面">
    public boolean isOpenKettle(final UUID uuid) {
        return defaultContents.containsKey(uuid);
    }
    
    public void setDefaultContents(final UUID uuid, final ItemStack[] contents) {
        defaultContents.put(uuid, contents);
    }

    public List<CatalystBonus> getCatalystBonusList(final UUID uuid) {
        return catalystBonus.get(uuid);
    }

    public boolean hasCatalystBonus(final UUID uuid, final CatalystBonus bonus) {
        return catalystBonus.containsKey(uuid) && catalystBonus.get(uuid).contains(bonus);
    }

    public void addCatalystBonus(final UUID uuid, final CatalystBonus data) {
        if (catalystBonus.containsKey(uuid)) {
            catalystBonus.get(uuid).add(data);
            return;
        }
        final List<CatalystBonus> list = new ArrayList<CatalystBonus>() {
            @Override
            public boolean contains(Object obj) {
                if (!(obj instanceof CatalystBonus)) {
                    Chore.log("not contains : " + obj);
                }
                return super.contains(obj);
            }
        };
        list.add(data);
        catalystBonus.put(uuid, list);
    }

    public boolean removeCatalystBonus(final UUID uuid, final CatalystBonus data) {
        if (catalystBonus.containsKey(uuid)) {
            return catalystBonus.get(uuid).remove(data);
        }
        return false;
    }

    public void addKettleData(final UUID uuid, final ItemStack item, final int csize, final Map<Integer, Integer> rslots, final int rotate, final int rlud) {
        if (!kettleData.containsKey(uuid)) {
            kettleData.put(uuid, new KettleBox(csize));
        }
        kettleData.get(uuid).addItem(item, rslots, rotate, rlud);
    }

    public KettleBox getKettleData(final UUID uuid) {
        return kettleData.get(uuid);
    }

    @NotNull
    public List<Characteristic> getCharacteristics(final UUID uuid) {
        final List<Characteristic> cs = characteristics.get(uuid);
        if (cs == null) {
            return new ArrayList<>(0);
        }
        final List<Characteristic> result = new ArrayList<>();
        cs.stream().filter(c -> (!result.contains(c))).forEachOrdered(result::add);
        final List<Characteristic> ccs = catalystCharacteristics.get(uuid);
        if (ccs != null) {
            ccs.stream().filter(c -> (!result.contains(c))).forEachOrdered(result::add);
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
            if (!catalystCharacteristics.containsKey(uuid)) {
                catalystCharacteristics.put(uuid, new ArrayList<>());
            }
            final List<Characteristic> cs = catalystCharacteristics.get(uuid);
            if(!cs.contains(characteristic)) {
                cs.add(characteristic);
            }
        }
    }

    public void removeCatalystCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (catalystCharacteristics.containsKey(uuid)) {
            catalystCharacteristics.get(uuid).remove(characteristic);
        }
    }

    public List<Characteristic> getSelectCharacteristics(final UUID uuid) {
        return selectCharacteristics.get(uuid);
    }

    public void addSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (!selectCharacteristics.containsKey(uuid)) {
            selectCharacteristics.put(uuid, new ArrayList<>());
        }
        final List<Characteristic> cs = selectCharacteristics.get(uuid);
        if (!cs.contains(characteristic)) {
            cs.add(characteristic);
        }
    }

    public void removeSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (selectCharacteristics.containsKey(uuid)) {
            selectCharacteristics.get(uuid).remove(characteristic);
        }
    }

    public boolean isSelectCharacteristic(final UUID uuid, final Characteristic characteristic) {
        if (selectCharacteristics.containsKey(uuid)) {
            return selectCharacteristics.get(uuid).contains(characteristic);
        }
        return false;
    }

    public void resetSelectCharacteristic(final UUID uuid) {
        selectCharacteristics.remove(uuid);
    }
    //</editor-fold>


}
