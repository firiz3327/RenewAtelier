package net.firiz.renewatelier.utils.doubledata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FinalDoubleData<L, R> extends DoubleData<L, R> {

    public FinalDoubleData(L left, R right) {
        super(left, right);
    }

    @Override
    @NotNull
    public L getLeft() {
        return Objects.requireNonNull(getLeft());
    }

    @Override
    @Deprecated
    public void setLeft(@Nullable L left) {
        throw new UnsupportedOperationException("Use DoubleData class.");
    }

    @Override
    @NotNull
    public R getRight() {
        return Objects.requireNonNull(getRight());
    }

    @Override
    @Deprecated
    public void setRight(@Nullable R right) {
        throw new UnsupportedOperationException("Use DoubleData class.");
    }

}
