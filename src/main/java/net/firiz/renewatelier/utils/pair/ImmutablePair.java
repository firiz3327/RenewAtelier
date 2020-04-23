package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImmutablePair<L, R> implements PairInterface<L, R> {

    @NotNull
    private final L left;
    @NotNull
    private final R right;

    public ImmutablePair(@NotNull L left, @NotNull R right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
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
