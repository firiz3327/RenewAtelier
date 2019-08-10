package net.firiz.renewatelier.alchemy.kettle.box;

import java.util.Map;

public class KettleBoxData {

    private final Map<Integer, Integer> rslots;
    private final int rotate; // 回転
    private final int rlud; // 左右上下反転

    public KettleBoxData(Map<Integer, Integer> rslots, int rotate, int rlud) {
        this.rslots = rslots;
        this.rotate = rotate;
        this.rlud = rlud;
    }

    public Map<Integer, Integer> getRSlots() {
        return rslots;
    }

    public int getRotate() {
        return rotate;
    }

    public int getRLUD() {
        return rlud;
    }
}
