package net.firiz.renewatelier.characteristic.datas.addattack.x;

public abstract class ObjectAddAttack<X, Y> implements AddAttackX {

    private final X x;
    private final Y y;

    public ObjectAddAttack(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public final X getX() {
        return x;
    }

    public final Y getY() {
        return y;
    }
}
