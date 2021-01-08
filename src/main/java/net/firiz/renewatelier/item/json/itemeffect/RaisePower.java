package net.firiz.renewatelier.item.json.itemeffect;

public class RaisePower implements IItemEffect {

    private final double power;
    private final boolean multiply;

    public RaisePower(double power, boolean multiply) {
        this.power = power;
        this.multiply = multiply;
    }

    public double getPower() {
        return power;
    }

    public boolean isMultiply() {
        return multiply;
    }

}
