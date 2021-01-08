package net.firiz.renewatelier.utils.java;

import java.lang.reflect.Array;
import java.util.Arrays;
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

    public static <T> T[] push(T[] array, T object) {
        final T[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[newArray.length - 1] = object;
        return newArray;
    }

    public static <T> T[] unshift(T[] array, T object) {
        final Class<? extends Object[]> type = array.getClass();
        final int newLength = array.length + 1;
        @SuppressWarnings("unchecked") final T[] newArray = type == Object[].class ? (T[]) new Object[newLength] : (T[]) Array.newInstance(type.getComponentType(), newLength);
        newArray[0] = object;
        System.arraycopy(array, 0, newArray, 1, Math.min(array.length, newLength));
        return newArray;
    }

}
