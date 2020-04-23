package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NonNullPair<L, R> implements PairInterface<L, R> {

    @NotNull
    private L left;
    @NotNull
    private R right;

    public NonNullPair(@NotNull L left, @NotNull R right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    public void setLeft(@NotNull L left) {
        this.left = Objects.requireNonNull(left);
    }

    @Override
    @NotNull
    public L getLeft() {
        return left;
    }

    public void setRight(@NotNull R right) {
        this.right = Objects.requireNonNull(right);
    }

    @Override
    @NotNull
    public R getRight() {
        return right;
    }

}
