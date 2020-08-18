package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.entity.Race;

public class ChInt2Race implements ChData {

    private final int x;
    private final Race race;

    private ChInt2Race(int x, Race race) {
        this.x = x;
        this.race = race;
    }

    public static ChData newInstance(String[] args) {
        return new ChInt2Race(Integer.parseInt(args[0]), Race.valueOf(args[1]));
    }

    public int getX() {
        return x;
    }

    public Race getRace() {
        return race;
    }
}
