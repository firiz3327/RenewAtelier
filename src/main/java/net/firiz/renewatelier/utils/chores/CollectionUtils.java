package net.firiz.renewatelier.utils.chores;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    private CollectionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends List<?>> T castList(Object obj) {
        return (T) obj;
    }

    public static int[] parseInts(final List<Integer> list) {
        final int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static List<Integer> parseInts(final int[] array) {
        final List<Integer> result = new ArrayList<>();
        for (int val : array) {
            result.add(val);
        }
        return result;
    }
}
