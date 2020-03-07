package net.firiz.renewatelier.utils.doubledata;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NonNullPair<L, R> extends DoubleData<L, R> {

    public NonNullPair(@NotNull L left, @NotNull R right) {
        super(Objects.requireNonNull(left), Objects.requireNonNull(right));
    }

    @Override
    public void setLeft(@NotNull L left) {
        super.setLeft(Objects.requireNonNull(left));
    }

    @Override
    @NotNull
    public L getLeft() {
        assert super.getLeft() != null;
        return super.getLeft();
    }

    @Override
    public void setRight(@NotNull R right) {
        super.setRight(Objects.requireNonNull(right));
    }

    @Override
    @NotNull
    public R getRight() {
        assert super.getRight() != null;
        return super.getRight();
    }

}
