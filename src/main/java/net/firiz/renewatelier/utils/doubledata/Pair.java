package net.firiz.renewatelier.utils.doubledata;

import org.jetbrains.annotations.Nullable;

public class Pair<L, R> {

    private L left;
    private R right;

    public Pair() {
        this.left = null;
        this.right = null;
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
        return "DoubleData[" + left.toString() + ", " + right.toString() + "]";
    }

}
