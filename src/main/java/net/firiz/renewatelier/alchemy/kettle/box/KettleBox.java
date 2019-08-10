/*
 * KettleBox.java
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
package net.firiz.renewatelier.alchemy.kettle.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.kettle.BonusItem;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.utils.DoubleData;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class KettleBox {

    // 設置されたアイテムとそのアイテムの配置とcircleValue
    private final DoubleData<BonusItem, KettleBoxData>[] items;
    private final List<CatalystBonus>[] usedBonus;

    public KettleBox(int count) {
        this.items = new DoubleData[count];
        this.usedBonus = new List[count];
    }

    /**
     * データを前の状態に戻して、削除対象のデータを返します
     *
     * @return 削除対象のデータ
     */
    public final DoubleData<BonusItem, KettleBoxData> backData(final int rotate, final int rlud) {
        for (int i = 1; i <= items.length; i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items[items.length - i];
            if (data != null) {
                items[items.length - i] = null;
                final int sel = items.length - i - 1;
                if (sel >= 0) {
                    usedBonus[sel] = null;
                }
                final KettleBoxData kbd = data.getRight();
                final ItemStack item = data.getLeft().getItem();
                int[] size = MaterialSize.getSize(item);
                if (kbd.getRotate() != 0) {
                    for (int j = 0; j < 4 - kbd.getRotate(); j++) {
                        size = MaterialSize.right_rotation(size);
                    }
                }
                if (kbd.getRLUD() != 0) {
                    size = getRLUDTypeSize(kbd.getRLUD(), size);
                }
                for (int j = 0; j < rotate; j++) {
                    size = MaterialSize.right_rotation(size);
                }
                size = getRLUDTypeSize(rlud, size);
                item.setItemMeta(MaterialSize.setSize(item, size));
                return data;
            }
        }
        return null;
    }

    private int[] getRLUDTypeSize(int rlud, int[] size) {
        switch (rlud) {
            case 1:
                return MaterialSize.right_left_turn(size);
            case 2:
                return MaterialSize.up_down_turn(size);
            case 3:
                return MaterialSize.up_down_turn(MaterialSize.right_left_turn(size));
        }
        return size;
    }

    public final void addItem(final ItemStack item, final Map<Integer, Integer> rslots, final int rotate, final int rlud) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = new DoubleData<>(new BonusItem(item), new KettleBoxData(rslots, rotate, rlud));
                break;
            }
        }
    }

    public final boolean usedBonus(final CatalystBonus cb) {
        for (final List<CatalystBonus> bonus : usedBonus) {
            if (bonus != null) {
                if (bonus.contains(cb)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final void addBonus(final CatalystBonus cb) {
        for (int i = 1; i <= items.length; i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items[items.length - i];
            if (data != null) {
                final int sel = items.length - i;
                if (usedBonus[sel] == null) {
                    final List<CatalystBonus> cbs = new ArrayList<>();
                    cbs.add(cb);
                    usedBonus[sel] = cbs;
                } else {
                    usedBonus[sel].add(cb);
                }
                break;
            }
        }
    }

    public final List<CatalystBonus> getBonus() {
        for (int i = 1; i <= items.length; i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items[items.length - i];
            if (data != null) {
                final int select = items.length - i - 1; // 4:3 3:2
                return select >= 0 ? usedBonus[select] : null;
            }
        }
        return null;
    }

    public final List<ItemStack> getItemStacks() {
        final List<ItemStack> result = new ArrayList<>();

        for (final DoubleData<BonusItem, KettleBoxData> data : items) {
            if (data != null) {
                result.add(data.getLeft().getItem());
            }
        }
        return result;

    }

    public final List<BonusItem> getItems() {
        final List<BonusItem> result = new ArrayList<>();

        for (final DoubleData<BonusItem, KettleBoxData> data : items) {
            if (data != null) {
                result.add(data.getLeft());
            }
        }
        return result;

    }

    public final Map<Integer, Integer> getSlots() {
        return items[items.length - 1].getRight().getRSlots();
    }

    public final int getCSize() {
        return items.length;
    }

    /**
     *
     * @return <slot, itemStack>, value
     */
    public final Map<DoubleData<Integer, BonusItem>, Integer> getResultCS() {
        final Map<DoubleData<Integer, BonusItem>, Integer> result = new HashMap<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = getOverlap();
        overlap.keySet().forEach((layer) -> {
            final Map<Integer, Integer> datas = overlap.get(layer);
            datas.keySet().forEach((slot) -> result.put(new DoubleData<>(slot, layer.getRight()), datas.get(slot)));
        });

        return result;

    }

    public final List<BonusItem> getResultItems() {
        final List<BonusItem> result = new ArrayList<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = getOverlap();
        overlap.keySet().forEach((layer) -> result.add(layer.getRight()));

        return result;
    }

    private Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> getOverlap() {
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = new LinkedHashMap<>();

        for (int i = 0; i < items.length; i++) {
            final DoubleData<BonusItem, KettleBoxData> dd = items[i];

            if (dd != null) {
                final Map<Integer, Integer> rslots = dd.getRight().getRSlots();

                for (final DoubleData<Integer, BonusItem> layer : new LinkedHashMap<>(overlap).keySet()) {
                    boolean checkOver = false;

                    loop_checkOver:
                    for (final int slot : overlap.get(layer).keySet()) {
                        for (final int ddslot : rslots.keySet()) {
                            if (slot == ddslot) {
                                checkOver = true;
                                break loop_checkOver;
                            }
                        }
                    }

                    if (checkOver) {
                        overlap.remove(layer);
                    }
                }
                overlap.put(new DoubleData<>(i, dd.getLeft()), rslots);
            }
        }
        return overlap;
    }

}
