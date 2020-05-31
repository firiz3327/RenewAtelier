package net.firiz.renewatelier.utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class FakeId {

    private FakeId() {
    }

    private static final int CHECK_VALUE = Integer.MIN_VALUE + 1000;
    private static final AtomicInteger value = new AtomicInteger();

    public static int createId() {
        final int v = value.decrementAndGet();
        if (v <= CHECK_VALUE) {
            value.set(0);
        }
        return v;
    }

}
