package net.firiz.renewatelier.utils.doubledata;

import org.jetbrains.annotations.NotNull;

public class FinalDoubleData<L, R> {

    private final L left;
    private final R right;

    public FinalDoubleData(@NotNull L left, @NotNull R right) {
        this.left = left;
        this.right = right;
    }

    @NotNull
    public L getLeft() {
        return left;
    }

    @NotNull
    public R getRight() {
        return right;
    }

}
