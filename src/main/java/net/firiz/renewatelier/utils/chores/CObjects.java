package net.firiz.renewatelier.utils.chores;

import java.util.function.Function;

public final class CObjects {

    private CObjects() {
    }

    public static <T, O> T nullIf(O obj, Function<O, T> returnFunction, T elseValue) {
        return obj == null ? elseValue : returnFunction.apply(obj);
    }

}
