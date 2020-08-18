package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.utils.CommonUtils;

public class ChBool implements ChData {

    private final boolean x;

    private ChBool(int x) {
        this.x = 0 < x;
    }

    private ChBool(boolean x) {
        this.x = x;
    }

    public static ChData newInstance(String[] args) {
        if (CommonUtils.isNumMatch(args[0])) {
            return new ChBool(Integer.parseInt(args[0]));
        }
        return new ChBool(Boolean.parseBoolean(args[0]));
    }

    public boolean isX() {
        return x;
    }
}
