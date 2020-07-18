package net.firiz.renewatelier.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GArray<T> implements Iterable<T> {

    private final Object[] elementData;

    public GArray(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }

    public void set(int i, T e) {
        elementData[i] = e;
    }

    @SuppressWarnings("unchecked")
    public T get(int i) {
        return (T) elementData[i];
    }

    public int length() {
        return elementData.length;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return StreamSupport.stream(spliterator(), false).iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        stream().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliterator(elementData, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @NotNull
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
