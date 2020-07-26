package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.Nullable;

public class ImmutableNullablePair<L, R> implements PairInterface<L, R> {

    @Nullable
    private final L left;
    @Nullable
    private final R right;

    public ImmutableNullablePair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public L getLeft() {
        return left;
    }

    @Nullable
    public R getRight() {
        return right;
    }
}
