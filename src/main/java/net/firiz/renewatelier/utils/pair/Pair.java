package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.Nullable;

public class Pair<L, R> implements PairInterface<L, R> {

    private L left;
    private R right;

    public Pair() {
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    @Nullable
    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return info();
    }

}
