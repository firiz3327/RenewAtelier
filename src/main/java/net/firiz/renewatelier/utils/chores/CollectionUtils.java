package net.firiz.renewatelier.utils.chores;

import java.util.List;
import java.util.function.IntConsumer;

public class CollectionUtils {

    private CollectionUtils() {
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
}
