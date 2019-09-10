package net.firiz.renewatelier.utils;

import org.jetbrains.annotations.Nullable;

public class DoubleData<L, R> {

    private L left;
    private R right;

    public DoubleData() {
        this.left = null;
        this.right = null;
    }

    public DoubleData(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public L getLeft() {
        return left;
    }

    public void setLeft(@Nullable L left) {
        this.left = left;
    }

    @Nullable
    public R getRight() {
        return right;
    }

    public void setRight(@Nullable R right) {
        this.right = right;
    }
}
