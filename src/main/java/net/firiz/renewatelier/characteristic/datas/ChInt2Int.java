package net.firiz.renewatelier.characteristic.datas;

public class ChInt2Int implements ChData {

    private final int x;
    private final int y;

    private ChInt2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static ChData newInstance(String[] args) {
        return new ChInt2Int(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
