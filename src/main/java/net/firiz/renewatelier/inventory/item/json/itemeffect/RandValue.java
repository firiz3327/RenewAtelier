package net.firiz.renewatelier.inventory.item.json.itemeffect;

import net.firiz.renewatelier.utils.Randomizer;

public class RandValue implements IItemEffect {

    private final Mode mode;
    private final int min;
    private final int max;

    public RandValue(Mode mode, int min, int max) {
        this.mode = mode;
        this.min = min;
        this.max = max;
    }

    public int rand() {
        return Randomizer.rand(min, max);
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        DAMAGE,
        HP,
        MP
    }

}
