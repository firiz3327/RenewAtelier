package net.firiz.renewatelier.alchemy.kettle.box;

import java.util.*;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.kettle.bonus.BonusItem;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.GArray;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.firiz.renewatelier.utils.pair.Pair;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class KettleBox {

    // 設置されたアイテムとそのアイテムの配置とcircleValue
    private final GArray<Pair<BonusItem, KettleBoxCircleData>> items;
    private final GArray<List<CatalystBonus>> usedBonus;

    public KettleBox(int count) {
        this.items = new GArray<>(count);
        this.usedBonus = new GArray<>(count);
    }

    /**
     * データを前の状態に戻して、削除対象のデータを返します
     *
     * @return 削除対象のデータ
     */
    public final Pair<BonusItem, KettleBoxCircleData> backData(final int rotate, final int rlud) {
        for (int i = 1; i <= items.length(); i++) {
            final Pair<BonusItem, KettleBoxCircleData> data = items.get(items.length() - i);
            if (data != null) {
                items.set(items.length() - i, null);
                final int sel = items.length() - i - 1;
                if (sel >= 0) {
                    usedBonus.set(sel, null);
                }
                final KettleBoxCircleData kbd = data.getRight();
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
                AlchemyItemStatus.setSize(item, size);
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

    public final void addItem(final ItemStack item, final Int2IntMap rslots, final int rotate, final int rlud) {
        for (int i = 0; i < items.length(); i++) {
            if (items.get(i) == null) {
                items.set(i, new Pair<>(new BonusItem(item), new KettleBoxCircleData(rslots, rotate, rlud)));
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
        for (int i = 1; i <= items.length(); i++) {
            final Pair<BonusItem, KettleBoxCircleData> data = items.get(items.length() - i);
            if (data != null) {
                final int sel = items.length() - i;
                if (usedBonus.get(sel) == null) {
                    final List<CatalystBonus> cbs = new ObjectArrayList<>();
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
        for (int i = 1; i <= items.length(); i++) {
            final Pair<BonusItem, KettleBoxCircleData> data = items.get(items.length() - i);
            if (data != null) {
                final int select = items.length() - i - 1; // 4:3 3:2
                return select >= 0 ? usedBonus.get(select) : Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    public final List<ItemStack> getItemStacks() {
        final List<ItemStack> result = new ObjectArrayList<>();
        items.stream().filter(Objects::nonNull).forEach(data -> result.add(data.getLeft().getItem()));
        return result;

    }

    public final long getNonNullItemCount() {
        return items.stream().filter(Objects::nonNull).count();
    }

    public final List<BonusItem> getItems() {
        final List<BonusItem> result = new ObjectArrayList<>();
        items.stream().filter(Objects::nonNull).forEach(data -> result.add(data.getLeft()));
        return result;

    }

    public final Int2IntMap getSlots() {
        return items.get(items.length() - 1).getRight().getRSlots();
    }

    public final int getCSize() {
        return items.length();
    }

    /**
     * @return <slot, itemStack>, value
     */
    public final Object2IntMap<Pair<Integer, BonusItem>> getResultCS() {
        final Object2IntMap<Pair<Integer, BonusItem>> result = new Object2IntOpenHashMap<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<Pair<Integer, BonusItem>, Int2IntMap> overlap = getOverlap();
        overlap.keySet().forEach(layer -> {
            final Int2IntMap datas = overlap.get(layer);
            datas.keySet().forEach(CollectionUtils.intConsumer(slot -> result.put(new Pair<>(slot, layer.getRight()), datas.get(slot))));
        });

        return result;

    }

    public final List<BonusItem> getResultItems() {
        final List<BonusItem> result = new ObjectArrayList<>();

        // 配置
        // <layer, itemStack>, <slot, value>
        final Map<Pair<Integer, BonusItem>, Int2IntMap> overlap = getOverlap();
        overlap.keySet().forEach(layer -> result.add(layer.getRight()));

        return result;
    }

    private Map<Pair<Integer, BonusItem>, Int2IntMap> getOverlap() {
        final Map<Pair<Integer, BonusItem>, Int2IntMap> overlap = new Object2ObjectLinkedOpenHashMap<>();
        for (int i = 0; i < items.length(); i++) {
            final Pair<BonusItem, KettleBoxCircleData> dd = items.get(i);

            if (dd != null) {
                final Int2IntMap rslots = dd.getRight().getRSlots();

                for (final Pair<Integer, BonusItem> layer : new Object2ObjectLinkedOpenHashMap<>(overlap).keySet()) {
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
                overlap.put(new Pair<>(i, dd.getLeft()), rslots);
            }
        }
        return overlap;
    }

}
