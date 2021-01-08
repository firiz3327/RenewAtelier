package net.firiz.renewatelier.alchemy.kettle.bonus;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.utils.java.CollectionUtils;

import java.util.List;

class BonusPlayerData {

    private final int req;
    private final IntList bars;
    private final List<AlchemyAttribute[]> ups;
    private final Int2ObjectMap<AlchemyAttribute[]> levelUps;

    BonusPlayerData(int req) {
        this.req = req;
        this.bars = new IntArrayList();
        this.ups = new ObjectArrayList<>();
        this.levelUps = new Int2ObjectLinkedOpenHashMap<>();
    }

    int getReq() {
        return req;
    }

    int getBar() {
        int bar = bars.size() - 1;
        if (bar != -1) {
            return bars.getInt(bar) % req;
        }
        return 0;
    }

    public int getLevel() {
        int bar = bars.size() - 1;
        if (bar != -1) {
            return bars.getInt(bar) / req;
        }
        return 0;
    }

    AlchemyAttribute[] getUp() {
        return ups.get(ups.size() - 1);
    }

    List<AlchemyAttribute[]> getLevelUps() {
        List<AlchemyAttribute[]> result = new ObjectArrayList<>();
        levelUps.keySet().forEach(CollectionUtils.intConsumer(value -> result.add(levelUps.get(value))));
        return result;
    }

    public void add(int plus, AlchemyAttribute[] ups) {
        int oldLevel = getLevel();
        bars.add(plus + (bars.isEmpty() ? 0 : bars.getInt(bars.size() - 1)));
        this.ups.add(ups);
        if (oldLevel < getLevel()) {
            levelUps.put(bars.size() - 1, ups);
        }
    }

    void back() {
        final int removeIndex = bars.size() - 1;
        if (removeIndex != -1) {
            bars.removeInt(removeIndex);
            ups.remove(removeIndex);
            levelUps.remove(removeIndex);
        }
    }
}