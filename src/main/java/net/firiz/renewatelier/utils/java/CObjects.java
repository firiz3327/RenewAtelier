package net.firiz.renewatelier.utils.java;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CObjects {

    private CObjects() {
    }

    @Contract("null, _, _ -> param3")
    public static <T, O> T nullIfFunction(@Nullable O obj, @NotNull Function<O, T> returnFunction, @Nullable T elseValue) {
        return obj == null ? elseValue : returnFunction.apply(obj);
    }

    public static <T, O> Optional<T> nullIfOptional(O obj, Function<O, T> returnFunction) {
        return obj == null ? Optional.empty() : Optional.ofNullable(returnFunction.apply(obj));
    }

    public static <O> boolean nullIfPredicate(O obj, Predicate<O> returnFunction, boolean elseValue) {
        return obj == null ? elseValue : returnFunction.test(obj);
    }

    public static <O> void nonNullConsumer(O obj, Consumer<O> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public static <R> R supplier(Supplier<R> supplier) {
        return supplier.get();
    }

}
