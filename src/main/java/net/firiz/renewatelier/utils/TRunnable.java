package net.firiz.renewatelier.utils;

@FunctionalInterface
public interface TRunnable<T, Z> {

    Z run(T t);

}
