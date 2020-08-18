package net.firiz.renewatelier.characteristic.datas;

public class ChInt implements ChData {

    private final int x;

    public ChInt(int x) {
        this.x = x;
    }

    public static ChData newInstance(String[] args) {
        return new ChInt(Integer.parseInt(args[0]));
    }

    public int getX() {
        return x;
    }

}
