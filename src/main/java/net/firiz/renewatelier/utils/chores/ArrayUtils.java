package net.firiz.renewatelier.utils.chores;

import java.util.Objects;

public class ArrayUtils {

    private ArrayUtils() {
    }

    public static int[] convertToInt(String[] stringArray) {
        Objects.requireNonNull(stringArray);
        final int[] result = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = Integer.parseInt(stringArray[i]);
        }
        return result;
    }

    public static String[] convertToString(int[] intArray) {
        Objects.requireNonNull(intArray);
        final String[] result = new String[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = String.valueOf(intArray[i]);
        }
        return result;
    }

    public static String splitString(int[] array) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

}
