package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Triple<L, M, R> {

    @NotNull
    private final L left;
    @NotNull
    private final M middle;
    @NotNull
    private final R right;

    public Triple(@NotNull L left, @NotNull M middle, @NotNull R right) {
        this.left = Objects.requireNonNull(left);
        this.middle = Objects.requireNonNull(middle);
        this.right = Objects.requireNonNull(right);
    }

    @NotNull
    public L getLeft() {
        return left;
    }

    @NotNull
    public M getMiddle() {
        return middle;
    }

    @NotNull
    public R getRight() {
        return right;
    }

}
