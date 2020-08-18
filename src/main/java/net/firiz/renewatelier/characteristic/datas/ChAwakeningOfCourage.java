package net.firiz.renewatelier.characteristic.datas;

public class ChAwakeningOfCourage implements ChData {

    // 強敵補正・ボス補正
    private final boolean boss;
    private final int x;

    private ChAwakeningOfCourage(boolean boss, int x) {
        this.boss = boss;
        this.x = x;
    }

    public static ChData newInstance(String[] args) {
        return new ChAwakeningOfCourage(args[0].equalsIgnoreCase("boss"), Integer.parseInt(args[1]));
    }

    public boolean isBoss() {
        return boss;
    }

    public int getX() {
        return x;
    }
}
