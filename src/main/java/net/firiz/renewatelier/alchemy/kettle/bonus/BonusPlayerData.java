package net.firiz.renewatelier.alchemy.kettle.bonus;


import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class BonusPlayerData {

    private final int req;
    private final List<Integer> bars;
    private final List<AlchemyAttribute[]> ups;
    private final Map<Integer, AlchemyAttribute[]> levelups;

    BonusPlayerData(int req) {
        this.req = req;
        this.bars = new ArrayList<>();
        this.ups = new ArrayList<>();
        this.levelups = new LinkedHashMap<>();
    }

    int getReq() {
        return req;
    }

    int getBar() {
        int bar = bars.size() - 1;
        if (bar != -1) {
            return bars.get(bar) % req;
        }
        return 0;
    }

    public int getLevel() {
        int bar = bars.size() - 1;
        if (bar != -1) {
            return bars.get(bar) / req;
        }
        return 0;
    }

    AlchemyAttribute[] getUp() {
        return ups.get(ups.size() - 1);
    }

    List<AlchemyAttribute[]> getLevelUps() {
        List<AlchemyAttribute[]> result = new ArrayList<>();
        levelups.keySet().forEach(i -> result.add(levelups.get(i)));
        return result;
    }

    public void add(int plus, AlchemyAttribute[] ups) {
        int oldLevel = getLevel();
        bars.add(plus + (bars.isEmpty() ? 0 : bars.get(bars.size() - 1)));
        this.ups.add(ups);
        if (oldLevel < getLevel()) {
            levelups.put(bars.size() - 1, ups);
        }
    }

    void back() {
        int remove = bars.size() - 1;
        if (remove != -1) {
            bars.remove(remove);
            ups.remove(remove);
            levelups.remove(remove);
        }
    }
}