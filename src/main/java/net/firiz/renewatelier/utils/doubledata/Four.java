package net.firiz.renewatelier.utils.doubledata;

import org.jetbrains.annotations.NotNull;

public class Four<A, B, C, D> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;

    public Four(@NotNull A a, @NotNull B b, @NotNull C c, @NotNull D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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
