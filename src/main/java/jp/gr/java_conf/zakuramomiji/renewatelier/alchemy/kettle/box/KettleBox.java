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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonus;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.BonusItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class KettleBox {

    // 設置されたアイテムとそのアイテムの配置とcirclevalue
    private final DoubleData<BonusItem, Map<Integer, Integer>>[] items;
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
    public final DoubleData<BonusItem, Map<Integer, Integer>> backData() {
        for (int i = 1; i <= items.length; i++) {
            final DoubleData<BonusItem, Map<Integer, Integer>> data = items[items.length - i];
            if (data != null) {
                items[items.length - i] = null;
                final int sel = items.length - i - 1;
                if (sel >= 0) {
                    usedBonus[sel] = null;
                }
                return data;
            }
        }
        return null;
    }

    public final void addItem(final ItemStack item, final Map<Integer, Integer> rslots) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = new DoubleData<>(new BonusItem(item), rslots);
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
            final DoubleData<BonusItem, Map<Integer, Integer>> data = items[items.length - i];
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
            final DoubleData<BonusItem, Map<Integer, Integer>> data = items[items.length - i];
            if (data != null) {
                final int select = items.length - i - 1; // 4:3 3:2
                return select >= 0 ? usedBonus[select] : null;
            }
        }
        return null;
    }

    public final List<ItemStack> getItemStacks() {
        final List<ItemStack> result = new ArrayList<>();

        for (final DoubleData<BonusItem, Map<Integer, Integer>> data : items) {
            if (data != null) {
                result.add(data.getLeft().getItem());

            }
        }
        return result;

    }

    public final List<BonusItem> getItems() {
        final List<BonusItem> result = new ArrayList<>();

        for (final DoubleData<BonusItem, Map<Integer, Integer>> data : items) {
            if (data != null) {
                result.add(data.getLeft());

            }
        }
        return result;

    }

    public final Map<Integer, Integer> getSlots() {
        return items[items.length - 1].getRight();
    }

    public final int getCSize() {
        return items.length;
    }

    /**
     *
     * @return <slot, itemstack>, value
     */
    public final Map<DoubleData<Integer, BonusItem>, Integer> getResultCS() {
        final Map<DoubleData<Integer, BonusItem>, Integer> result = new HashMap<>();

        // 配置
        // <layer, itemstack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = new LinkedHashMap<>();

        for (int i = 0; i < items.length; i++) {
            final DoubleData<BonusItem, Map<Integer, Integer>> dd = items[i];

            if (dd != null) {
                final Map<Integer, Integer> right = dd.getRight();

                for (final DoubleData<Integer, BonusItem> layer : new LinkedHashMap<>(overlap).keySet()) {
                    boolean checkover = false;

                    loop_checkover:
                    for (final int slot : overlap.get(layer).keySet()) {
                        for (final int ddslot : right.keySet()) {
                            if (slot == ddslot) {
                                checkover = true;
                                break loop_checkover;
                            }
                        }
                    }

                    if (checkover) {
                        overlap.remove(layer);
                    }
                }
                overlap.put(new DoubleData<>(i, dd.getLeft()), right);
            }
        }
        overlap.keySet().forEach((layer) -> {
            final Map<Integer, Integer> datas = overlap.get(layer);
            datas.keySet().forEach((slot) -> {
                result.put(new DoubleData<>(slot, layer.getRight()), datas.get(slot));
            });
        });

        return result;

    }

    public final List<BonusItem> getResultItems() {
        final List<BonusItem> result = new ArrayList<>();

        // 配置
        // <layer, itemstack>, <slot, value>
        final Map<DoubleData<Integer, BonusItem>, Map<Integer, Integer>> overlap = new LinkedHashMap<>();

        for (int i = 0; i < items.length; i++) {
            final DoubleData<BonusItem, Map<Integer, Integer>> dd = items[i];

            if (dd != null) {
                final Map<Integer, Integer> right = dd.getRight();

                for (final DoubleData<Integer, BonusItem> layer : new LinkedHashMap<>(overlap).keySet()) {
                    boolean checkover = false;

                    loop_checkover:
                    for (final int slot : overlap.get(layer).keySet()) {
                        for (final int ddslot : right.keySet()) {
                            if (slot == ddslot) {
                                checkover = true;
                                break loop_checkover;
                            }
                        }
                    }

                    if (checkover) {
                        overlap.remove(layer);
                    }
                }
                overlap.put(new DoubleData<>(i, dd.getLeft()), right);
            }
        }
        overlap.keySet().forEach((layer) -> {
            result.add(layer.getRight());
        });

        return result;
    }

}
