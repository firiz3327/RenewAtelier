package net.firiz.renewatelier.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class UniqueList<T> extends ArrayList<T> {

    private static final String EXISTS_MESSAGE = "Similar data already exists.";
    private final UniqueCheck<T> equals;

    public UniqueList() {
        equals = Object::equals;
    }

    public UniqueList(@NotNull UniqueCheck<T> equals) {
        this.equals = equals;
    }

    public boolean containsO(T t) {
        final T tnn = Objects.requireNonNull(t);
        for(T t1 : this) {
            if (equals.run(t1, tnn)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(T t) {
        final T tnn = Objects.requireNonNull(t);
        for (T t1 : this) {
            if (equals.run(t1, tnn)) {
                throw new IllegalArgumentException(EXISTS_MESSAGE);
            }
        }
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t1 : this) {
            for (T t : c) {
                if (equals.run(t1, Objects.requireNonNull(t))) {
                    throw new IllegalArgumentException(EXISTS_MESSAGE);
                }
            }
        }
        return super.addAll(c);
    }

    @Override
    public T set(int index, T element) {
        final T t = Objects.requireNonNull(element);
        for (int i = 0; i < size(); i++) {
            if (i != index && equals.run(get(i), t)) {
                throw new IllegalArgumentException(EXISTS_MESSAGE);
            }
        }
        return super.set(index, element);
    }
}
