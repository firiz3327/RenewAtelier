package net.firiz.renewatelier.utils;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UniqueCheck<T> {

    boolean run(@NotNull T t1, @NotNull T t2);

}

