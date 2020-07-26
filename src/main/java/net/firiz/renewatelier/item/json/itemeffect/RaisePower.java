package net.firiz.renewatelier.item.json.itemeffect;

public class RaisePower {

    private final double power;
    private final boolean multiply;

    public RaisePower(double power, boolean multiply) {
        this.power = power;
        this.multiply = multiply;
    }

    public double raise(double base) {
        return multiply ? base * power : base + power;
    }

}
