package net.firiz.renewatelier.utils.java;

import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;

public class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> T getRandomValue(@NotNull final Collection<T> collection) {
        return get(collection, Randomizer.nextInt(collection.size()));
    }

    public static <T> T getRandomValue(@NotNull final T[] array) {
        return array[Randomizer.nextInt(array.length)];
    }

    @SafeVarargs
    public static <T> Collection<T> add(@NotNull final Collection<T> collection, final T... values) {
        collection.addAll(Arrays.asList(values));
        return collection;
    }

    public static <T> T get(@NotNull final Collection<T> collection, final int index) {
        return CommonUtils.cast(collection.toArray()[index]);
    }

    public static IntConsumer intConsumer(IntConsumer c) {
        return c;
    }

    public static int[] parseInts(final List<Integer> list) {
        final int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @SafeVarargs
    public static <T> Collection<T> addAll(Collection<T> origin, @NotNull final Collection<? extends T>... targets) {
        for (final Collection<? extends T> target : targets) {
            origin.addAll(target);
        }
        return origin;
    }

    @SafeVarargs
    public static <T> Set<T> addAll(Set<T> origin, @NotNull final Collection<? extends T>... targets) {
        for (final Collection<? extends T> target : targets) {
            origin.addAll(target);
        }
        return origin;
    }

    @SafeVarargs
    public static <T> List<T> addAll(List<T> origin, @NotNull final Collection<? extends T>... targets) {
        for (final Collection<? extends T> target : targets) {
            origin.addAll(target);
        }
        return origin;
    }
}
