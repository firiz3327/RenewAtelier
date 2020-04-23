package net.firiz.renewatelier.utils.pair;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Four<A, B, C, D> {
    @NotNull
    private final A a;
    @NotNull
    private final B b;
    @NotNull
    private final C c;
    @NotNull
    private final D d;

    public Four(@NotNull A a, @NotNull B b, @NotNull C c, @NotNull D d) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.c = Objects.requireNonNull(c);
        this.d = Objects.requireNonNull(d);
    }

    @NotNull
    public A getA() {
        return a;
    }

    @NotNull
    public B getB() {
        return b;
    }

    @NotNull
    public C getC() {
        return c;
    }
    @NotNull
    public D getD() {
        return d;
    }
}
