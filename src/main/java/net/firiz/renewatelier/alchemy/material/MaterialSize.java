/*
 * MaterialSize.java
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
package net.firiz.renewatelier.alchemy.material;

import net.firiz.renewatelier.alchemy.kettle.AlchemyCircle;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.Strings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author firiz
 */
public enum MaterialSize {
    // SIZE 1
    // ■□□
    // □□□
    // □□□
    S1_1(1),
    // SIZE 2
    // ■■□ ■□□ ■□■ ■□□
    // □□□ □■□ □□□ □□□
    // □□□ □□□ □□□ □□■
    S2_1(1, 1),
    S2_2(1, 0, 0, 0, 1),
    S2_3(1, 0, 1),
    S2_4(1, 0, 0, 0, 0, 0, 0, 0, 1),
    // SIZE 3
    // ■■■ ■■□ ■■□ ■□□ ■□■ 
    // □□□ ■□□ □■□ □■□ □■□
    // □□□ □□□ □□□ □□■ □□□
    S3_1(1, 1, 1),
    S3_2(1, 1, 0, 1),
    S3_3(1, 1, 0, 0, 1),
    S3_4(1, 0, 0, 0, 1, 0, 0, 0, 1),
    S3_5(1, 0, 1, 0, 1),
    S3_6(1, 1, 0, 0, 0, 1),
    // SIZE 4
    // □■□ ■■■ ■■□ ■□■ ■■□ ■□■ □■□
    // ■■■ ■□□ ■■□ □■□ □■■ □■□ ■□■
    // □□□ □□□ □□□ □□■ □□□ □■□ □■□
    S4_1(0, 1, 0, 1, 1, 1),
    S4_2(1, 1, 1, 1),
    S4_3(1, 1, 0, 1, 1),
    S4_4(1, 0, 1, 0, 1, 0, 0, 1),
    S4_5(1, 1, 0, 0, 1, 1),
    S4_6(1, 0, 1, 0, 1, 0, 0, 0, 1),
    S4_7(0, 1, 0, 1, 0, 1, 0, 1, 0),
    // SIZE 5
    // □■□ ■■■ ■■□ ■□□ ■■■ ■■□
    // ■■■ ■■□ □■■ □■□ □■□ □■■
    // □■□ □□□ □■□ ■■■ □■□ □□■
    S5_1(0, 1, 0, 1, 1, 1, 0, 1),
    S5_2(1, 1, 1, 1, 1),
    S5_3(1, 1, 0, 0, 1, 1, 0, 1),
    S5_4(1, 0, 0, 0, 1, 0, 1, 1, 1),
    S5_5(1, 1, 1, 0, 1, 0, 0, 1),
    S5_6(1, 1, 0, 0, 1, 1, 0, 0, 1),
    // SIZE 6
    // ■■■ ■■□ ■□□ ■□■ ■□□ ■■□ ■□■
    // ■■■ ■■■ ■■■ ■■■ ■■■ □■■ □■□
    // □□□ □■□ ■■□ □■□ ■□■ ■□■ ■■■
    S6_1(1, 1, 1, 1, 1, 1),
    S6_2(1, 1, 0, 1, 1, 1, 0, 1),
    S6_3(1, 0, 0, 1, 1, 1, 1, 1),
    S6_4(1, 0, 1, 1, 1, 1, 0, 1),
    S6_5(1, 0, 0, 1, 1, 1, 1, 0, 1),
    S6_6(1, 1, 0, 0, 1, 1, 1, 0, 1),
    S6_7(1, 0, 1, 0, 1, 0, 1, 1, 1),
    // SIZE 7
    // ■■■ ■■■ ■■■ ■□■ ■■□ ■■□
    // ■■■ ■■■ □■■ ■■■ ■■■ ■■■
    // ■□□ □■□ ■□■ ■□■ □■■ ■□■
    S7_1(1, 1, 1, 1, 1, 1, 1),
    S7_2(1, 1, 1, 1, 1, 1, 0, 1),
    S7_3(1, 1, 1, 0, 1, 1, 1, 0, 1),
    S7_4(1, 0, 1, 1, 1, 1, 1, 0, 1),
    S7_5(1, 1, 0, 1, 1, 1, 0, 1, 1),
    S7_6(1, 1, 0, 1, 1, 1, 1, 0, 1),
    // SIZE 8
    // ■■■ ■■■ ■■■
    // ■■■ ■■■ ■□■
    // ■■□ ■□■ ■■■
    S8_1(1, 1, 1, 1, 1, 1, 1, 1),
    S8_2(1, 1, 1, 1, 1, 1, 1, 0, 1),
    S8_3(1, 1, 1, 1, 0, 1, 1, 1, 1),
    // SIZE 9
    // ■■■
    // ■■■
    // ■■■
    S9_1(1, 1, 1, 1, 1, 1, 1, 1, 1);

    private final int[][] ss;

    // windows
    //   1      2     3      4
    // ■■■ □■■ □□□ ■□□
    // ■□□ □□■ □□■ ■□□
    // □□□ □□■ ■■■ ■■□
    //   5      6     7      8
    // ■■■ ■■□ □□□ □□■
    // □□■ ■□□ ■□□ □□■
    // □□□ ■□□ ■■■ □■■
    //   9     10     11 S    12
    // □□□ □□■ ■■■ ■■□
    // ■□□ □□■ □□■ ■□□
    // ■■■ □■■ □□□ ■□□
    //   13    14     15     16
    // □□□ ■□□ ■■■ □■■
    // □□■ ■□□ ■□□ □□■
    // ■■■ ■■□ □□□ □□■
    // mac
    //   1     2     3     4
    //  ■■■   □■■   □□□   ■□□
    //  ■□□   □□■   □□■   ■□□
    //  □□□   □□■   ■■■   ■■□
    //   5     6     7     8
    //  ■■■   ■■□   □□□   □□■
    //  □□■   ■□□   ■□□   □□■
    //  □□□   ■□□   ■■■   □■■
    //   9     10    11    12
    //  □□□   □□■   ■■■   ■■□
    //  ■□□   □□■   □□■   ■□□
    //  ■■■   □■■   □□□   ■□□
    //   13    14    15    16
    //  □□□   ■□□   ■■■   □■■
    //  □□■   ■□□   ■□□   □□■
    //  ■■■   ■■□   □□□   □□■
    MaterialSize(int... ss) {
        this.ss = new int[16][9];
        if (ss.length < 9) {
            for (int i = 0; i < ss.length; i++) {
                this.ss[0][i] = ss.length <= i ? 0 : ss[i];
            }
        } else {
            this.ss[0] = ss;
        }

        // 通常回転・左右反転回転・上下反転回転・上下左右反転回転
        for (int i = 0; i < 4; i++) {
            for (int j = (i == 0 ? 1 : 0); j < 4; j++) {
                int[] ssj = Arrays.copyOf(this.ss[j - (i == 0 ? 1 : 0)], 9);
                switch (i) {
                    case 1:
                        ssj = right_left_turn(ssj);
                        break;
                    case 2:
                        ssj = up_down_turn(ssj);
                        break;
                    case 3:
                        ssj = right_left_turn(ssj);
                        ssj = up_down_turn(ssj);
                        break;
                }
                this.ss[i * 4 + j] = j == 0 ? ssj : i == 0 ? right_rotation(ssj) : ssj;
            }
        }
    }

    public int[] getSize(int i) {
        return ss[i];
    }

    public static int[] up_down_turn(int[] r) {
        final int[] turn = new int[]{6, 7, 8, 3, 4, 5, 0, 1, 2};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[turn[k]];
        }
        return a;
    }

    public static int[] right_left_turn(int[] r) {
        final int[] turn = new int[]{2, 1, 0, 5, 4, 3, 8, 7, 6};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[turn[k]];
        }
        return a;
    }

    public static int[] right_rotation(int[] r) {
        final int[] right_rotation = new int[]{6, 3, 0, 7, 4, 1, 8, 5, 2};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[right_rotation[k]];
        }
        return a;
    }

    public static int[] left_rotation(int[] r) {
        return right_rotation(right_rotation(right_rotation(r)));
    }

    public static int[] plusSize(int[] size) {
        final int color = size[0];
        for (int i = 1; i < size.length; i++) {
            int s = size[i];
            if (s == 0) {
                size[i] = color;
                break;
            }
        }
        return size;
    }

    public static int[] getSize(final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                final List<Integer> size = new ArrayList<>();
                for (final String lore : meta.getLore()) {
                    if (lore.contains("§7触媒:")) {
                        break;
                    }
                    if (lore.contains(Strings.W_W) || lore.contains(Strings.W_B)) {
                        String b = null;
                        for (char l : lore.toCharArray()) {
                            String j = String.valueOf(l);
                            switch (j) {
                                case Strings.W_W:
                                case Strings.W_B:
                                    size.add(AlchemyCircle.colorCint(b));
                                    break;
                                default:
                                    b = j;
                                    break;
                            }
                        }
                    }
                }
                if (size.size() == 9) {
                    return Chore.parseInts(size);
                }
            }
        }
        return null;
    }

    public static int getSizeCount(final ItemStack item) {
        final int[] size = getSize(item);
        int i = 0;
        for (int j : size) {
            if (j != 0) {
                i++;
            }
        }
        return i;
    }

    public static void setSize(final ItemStack item, final List<Integer> size) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> lores = meta.getLore();
        int start = -1;
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).contains(AlchemyItemStatus.SIZE.getCheck() + "§7サイズ:")) {
                start = i;
                break;
            }
        }
        if (start != -1) {
            StringBuilder str = new StringBuilder();
            int c = 0;
            for (final int i : size) {
                if (i == 0) {
                    str.append(Chore.intCcolor(i)).append(Strings.W_W);
                } else {
                    str.append(Chore.intCcolor(i)).append(Strings.W_B);
                }
                if (c >= 2) {
                    lores.set(start + 1, str.toString());
                    c = 0;
                    str = new StringBuilder();
                    start++;
                } else {
                    c++;
                }
            }
            meta.setLore(lores);
            item.setItemMeta(meta);
        }
    }
}
