package net.firiz.renewatelier.alchemy.material;

import java.util.Arrays;

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
    // ■■■ ■■□ ■■□ ■□□ ■□■ ■■□
    // □□□ ■□□ □■□ □■□ □■□ □□■
    // □□□ □□□ □□□ □□■ □□□ □□□
    S3_1(1, 1, 1),
    S3_2(1, 1, 0, 1),
    S3_3(1, 1, 0, 0, 1),
    S3_4(1, 0, 0, 0, 1, 0, 0, 0, 1),
    S3_5(1, 0, 1, 0, 1),
    S3_6(1, 1, 0, 0, 0, 1),
    // SIZE 4
    // □■□ ■■■ ■■□ ■□■ ■■□ ■□■ □■□
    // ■■■ ■□□ ■■□ □■□ □■■ □■□ ■□■
    // □□□ □□□ □□□ □■□ □□□ □□■ □■□
    S4_1(0, 1, 0, 1, 1, 1),
    S4_2(1, 1, 1, 1),
    S4_3(1, 1, 0, 1, 1),
    S4_4(1, 0, 1, 0, 1, 0, 0, 1),
    S4_5(1, 1, 0, 0, 1, 1),
    S4_6(1, 0, 1, 0, 1, 0, 0, 0, 1),
    S4_7(0, 1, 0, 1, 0, 1, 0, 1, 0),
    // SIZE 5
    // □■□ ■■■ ■■□ ■□□ ■■■ ■■□ ■□■
    // ■■■ ■■□ □■■ □■□ □■□ □■■ □■□
    // □■□ □□□ □■□ ■■■ □■□ □□■ □■■
    S5_1(0, 1, 0, 1, 1, 1, 0, 1),
    S5_2(1, 1, 1, 1, 1),
    S5_3(1, 1, 0, 0, 1, 1, 0, 1),
    S5_4(1, 0, 0, 0, 1, 0, 1, 1, 1),
    S5_5(1, 1, 1, 0, 1, 0, 0, 1),
    S5_6(1, 1, 0, 0, 1, 1, 0, 0, 1),
    S5_7(1, 0, 1, 0, 1, 0, 0, 1, 1),
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
            System.arraycopy(ss, 0, this.ss[0], 0, ss.length);
        } else {
            this.ss[0] = ss;
        }

        // 通常回転・左右反転回転・上下反転回転・上下左右反転回転
        for (int i = 0; i < 4; i++) {
            final int start = (i == 0 ? 1 : 0);
            for (int j = start; j < 4; j++) {
                int[] ssj = Arrays.copyOf(this.ss[j - start], 9);
                switch (i) {
                    case 1:
                        ssj = rightLeftTurn(ssj);
                        break;
                    case 2:
                        ssj = upDownTurn(ssj);
                        break;
                    case 3:
                        ssj = rightLeftTurn(ssj);
                        ssj = upDownTurn(ssj);
                        break;
                    default:
                        break;
                }
                if (j == 0) {
                    this.ss[i * 4 + j] = ssj;
                } else if (i == 0) {
                    this.ss[i * 4 + j] = rightRotation(ssj);
                } else {
                    this.ss[i * 4 + j] = ssj;
                }
            }
        }
    }

    public int[] getSize(int i) {
        return ss[i];
    }

    public static int[] upDownTurn(int[] r) {
        final int[] turn = new int[]{6, 7, 8, 3, 4, 5, 0, 1, 2};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[turn[k]];
        }
        return a;
    }

    public static int[] rightLeftTurn(int[] r) {
        final int[] turn = new int[]{2, 1, 0, 5, 4, 3, 8, 7, 6};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[turn[k]];
        }
        return a;
    }

    public static int[] rightRotation(int[] r) {
        final int[] rightRotation = new int[]{6, 3, 0, 7, 4, 1, 8, 5, 2};
        int[] a = new int[9];
        for (int k = 0; k < 9; k++) {
            a[k] = r[rightRotation[k]];
        }
        return a;
    }

    public static int[] leftRotation(int[] r) {
        return rightRotation(rightRotation(rightRotation(r)));
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
}
