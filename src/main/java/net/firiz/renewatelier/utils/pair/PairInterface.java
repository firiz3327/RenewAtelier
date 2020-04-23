package net.firiz.renewatelier.utils.pair;

public interface PairInterface<L, R> {

    L getLeft();
    R getRight();

    default String info() {
        return getClass().getSimpleName() + "[" + getLeft().toString() + ", " + getRight().toString() + "]";
    }

}
