package net.firiz.renewatelier.utils.chores;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CObjects {

    private CObjects() {
    }

    public static <T, O> T nullIfFunction(O obj, Function<O, T> returnFunction, T elseValue) {
        return obj == null ? elseValue : returnFunction.apply(obj);
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
