package net.firiz.renewatelier.alchemy.kettle.box;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class KettleBoxCircleData {

    private final Int2IntMap rslots;
    private final int rotate; // 回転
    private final int rlud; // 左右上下反転

    KettleBoxCircleData(Int2IntMap rslots, int rotate, int rlud) {
        this.rslots = rslots;
        this.rotate = rotate;
        this.rlud = rlud;
    }

    Int2IntMap getRSlots() {
        return rslots;
    }

    int getRotate() {
        return rotate;
    }

    int getRLUD() {
        return rlud;
    }
}
