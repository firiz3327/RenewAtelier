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
import net.firiz.renewatelier.alchemy.kettle.bonus.BonusItem;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class KettleBox {

    // 設置されたアイテムとそのアイテムの配置とcircleValue
    private final List<DoubleData<BonusItem, KettleBoxData>> items;
    private final List<List<CatalystBonus>> usedBonus;

    public KettleBox(int count) {
        this.items = new ArrayList<>(count);
        this.usedBonus = new ArrayList<>(count);
    }

    /**
     * データを前の状態に戻して、削除対象のデータを返します
     *
     * @return 削除対象のデータ
     */
    public final DoubleData<BonusItem, KettleBoxData> backData(final int rotate, final int rlud) {
        for (int i = 1; i <= items.size(); i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items.get(items.size() - i);
            if (data != null) {
                items.set(items.size() - i, null);
                final int sel = items.size() - i - 1;
                if (sel >= 0) {
                    usedBonus.set(sel, null);
                }
                final KettleBoxData kbd = data.getRight();
                final ItemStack item = data.getLeft().getItem();
                int[] size = AlchemyItemStatus.getSize(item);
                if (kbd.getRotate() != 0) {
                    for (int j = 0; j < 4 - kbd.getRotate(); j++) {
                        size = MaterialSize.rightRotation(size);
                    }
                }
                if (kbd.getRLUD() != 0) {
                    size = getRLUDTypeSize(kbd.getRLUD(), size);
                }
                for (int j = 0; j < rotate; j++) {
                    size = MaterialSize.rightRotation(size);
                }
                size = getRLUDTypeSize(rlud, size);
                item.setItemMeta(AlchemyItemStatus.setSize(item, size));
                return data;
            }
        }
        return null;
    }

    private int[] getRLUDTypeSize(int rlud, int[] size) {
        switch (rlud) {
            case 1:
                return MaterialSize.rightLeftTurn(size);
            case 2:
                return MaterialSize.upDownTurn(size);
            case 3:
                return MaterialSize.upDownTurn(MaterialSize.rightLeftTurn(size));
            default:
                return size;
        }
    }

    public final void addItem(final ItemStack item, final Map<Integer, Integer> rslots, final int rotate, final int rlud) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                items.set(i, new DoubleData<>(new BonusItem(item), new KettleBoxData(rslots, rotate, rlud)));
                break;
            }
        }
    }

    public final boolean usedBonus(final CatalystBonus cb) {
        for (final List<CatalystBonus> bonus : usedBonus) {
            if (bonus != null && bonus.contains(cb)) {
                return true;
            }
        }
        return false;
    }

    public final void addBonus(final CatalystBonus cb) {
        for (int i = 1; i <= items.size(); i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items.get(items.size() - i);
            if (data != null) {
                final int sel = items.size() - i;
                if (usedBonus.get(sel) == null) {
                    final List<CatalystBonus> cbs = new ArrayList<>();
                    cbs.add(cb);
                    usedBonus.set(sel, cbs);
                } else {
                    usedBonus.get(sel).add(cb);
                }
                break;
            }
        }
    }

    @NotNull
    public final List<CatalystBonus> getBonus() {
        for (int i = 1; i <= items.size(); i++) {
            final DoubleData<BonusItem, KettleBoxData> data = items.get(items.size() - i);
            if (data != null) {
                final int select = items.size() - i - 1; // 4:3 3:2
                return select >= 0 ? usedBonus.get(select) : new ArrayList<>(0);
            }
        }
        return new ArrayList<>(0);
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
        return items.get(items.size() - 1).getRight().getRSlots();
    }

    public final int getCSize() {
        return items.size();
    }

    /**
     * @return <slot, itemStack>, value
     */
    public final Map<DoubleData<Integer, BonusItem>, Integer> getResultCS() {
        final Map<DoubleData<Integer, BonusItem>, Integer> result = new HashMap<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = getOverlap();
        overlap.keySet().forEach(layer -> {
            final Map<Integer, Integer> datas = overlap.get(layer);
            datas.keySet().forEach(slot -> result.put(new DoubleData<>(slot, layer.getRight()), datas.get(slot)));
        });

        return result;

    }

    public final List<BonusItem> getResultItems() {
        final List<BonusItem> result = new ArrayList<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = getOverlap();
        overlap.keySet().forEach(layer -> result.add(layer.getRight()));

        return result;
    }

    private Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> getOverlap() {
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = new LinkedHashMap<>();
        for (int i = 0; i < items.size(); i++) {
            final DoubleData<BonusItem, KettleBoxData> dd = items.get(i);

            if (dd != null) {
                final Map<Integer, Integer> rslots = dd.getRight().getRSlots();

                for (final DoubleData<Integer, BonusItem> layer : new LinkedHashMap<>(overlap).keySet()) {
                    boolean checkOver = false;

                    loopCheckOver:
                    for (final int slot : overlap.get(layer).keySet()) {
                        for (final int ddslot : rslots.keySet()) {
                            if (slot == ddslot) {
                                checkOver = true;
                                break loopCheckOver;
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
