/*
 * Randomizer.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package net.firiz.renewatelier.utils;

import java.util.Random;

/**
 *
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

    /**
     * min以上、max以下の乱数を生成します。
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

}
