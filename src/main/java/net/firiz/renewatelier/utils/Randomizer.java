package net.firiz.renewatelier.utils;

import java.util.Random;

/**
 * @author firiz
 */
public final class Randomizer {

    private Randomizer() {
    }

    private static final Random RAND = new Random();
    private static final Sfmt RAND2 = new Sfmt(new int[]{(int) System.currentTimeMillis(), (int) Runtime.getRuntime().freeMemory()});

    public static int nextInt(final int arg0) {
        return RAND2.nextInt(arg0);
    }

    public static boolean nextBoolean() {
        return RAND.nextBoolean();
    }

    public static double nextDouble() {
        return RAND2.nextUnif();
    }

    public static long nextLong() {
        return RAND.nextLong();
    }

    public static float nextFloat() {
        return RAND.nextFloat();
    }

    /**
     * min以上、max以下の乱数を生成します。
     *
     * @param min 最低値
     * @param max 最大値
     * @return min以上、max以下の範囲で乱数を生成しそれを返します。
     */
    public static int rand(final int min, final int max) {
        return nextInt(max - min + 1) + min;
    }

    public static boolean percent(final int percent) {
        return percent(percent, 100);
    }

    public static boolean percent(final int percent, final int max) {
        return Randomizer.nextInt(max) < percent;
    }

    public static boolean percent(final double percent) {
        return Randomizer.nextDouble() < percent;
    }

}
