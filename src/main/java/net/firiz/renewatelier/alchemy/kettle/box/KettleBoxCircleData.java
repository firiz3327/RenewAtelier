package net.firiz.renewatelier.alchemy.kettle.box;

import java.util.Map;

public class KettleBoxCircleData {

    private final Map<Integer, Integer> rslots;
    private final int rotate; // 回転
    private final int rlud; // 左右上下反転

    KettleBoxCircleData(Map<Integer, Integer> rslots, int rotate, int rlud) {
        this.rslots = rslots;
        this.rotate = rotate;
        this.rlud = rlud;
    }

    Map<Integer, Integer> getRSlots() {
        return rslots;
    }

    int getRotate() {
        return rotate;
    }

    int getRLUD() {
        return rlud;
    }
}
