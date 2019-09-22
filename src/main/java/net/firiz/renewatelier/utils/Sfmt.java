package net.firiz.renewatelier.utils;

/*
Sfmt.java 乱数ライブラリ(44497)
coded by isaku@pb4.so-net.ne.jp
 */
final class Sfmt {

    int index;
    int coinBits;
    /* NextBit での残りのビット */
    int coinSave;
    /* NextBit での値保持 */
    int bytePos;
    /* NextByte で使用したバイト数 */
    int byteSave;
    /* NextByte での値保持 */
    int range;
    /* NextIntEx で前回の範囲 */
    int base;
    /* NextIntEx で前回の基準値 */
    int shift;
    /* NextIntEx で前回のシフト数 */
    int normalSw;
    /* NextNormal で残りを持っている */
    double normalSave;
    /* NextNormal の残りの値 */
    protected final int[] x = new int[1392];

    /* 状態テーブル */
    protected int[] getParity() {
        return new int[]{0x00000001, 0x00000000, 0xa3ac4000, 0xecc1327a};
    }

    public void genRandAll() {
        int a = 0;
        int b = 1320;
        int c = 1384;
        int d = 1388;
        int y;
        int[] p = x;

        do {
            y = p[a + 3] ^ (p[a + 3] << 24) ^ (p[a + 2] >>> 8) ^ ((p[b + 3] >>> 9) & 0x9ffd7bff);
            p[a + 3] = y ^ (p[c + 3] >>> 24) ^ (p[d + 3] << 5);
            y = p[a + 2] ^ (p[a + 2] << 24) ^ (p[a + 1] >>> 8) ^ ((p[b + 2] >>> 9) & 0xbfbf7bef);
            p[a + 2] = y ^ ((p[c + 2] >>> 24) | (p[c + 3] << 8)) ^ (p[d + 2] << 5);
            y = p[a + 1] ^ (p[a + 1] << 24) ^ (p[a] >>> 8) ^ ((p[b + 1] >>> 9) & 0xdfbebfff);
            p[a + 1] = y ^ ((p[c + 1] >>> 24) | (p[c + 2] << 8)) ^ (p[d + 1] << 5);
            y = p[a] ^ (p[a] << 24) ^ ((p[b] >>> 9) & 0xeffffffb);
            p[a] = y ^ ((p[c] >>> 24) | (p[c + 1] << 8)) ^ (p[d] << 5);
            c = d;
            d = a;
            a += 4;
            b += 4;
            if (b == 1392) {
                b = 0;
            }
        } while (a != 1392);
    }

    void periodCertification() {
        int work;
        int inner = 0;
        int i;
        int j;
        int[] parity = getParity();

        index = 1392;
        range = 0;
        normalSw = 0;
        coinBits = 0;
        bytePos = 0;
        for (i = 0; i < 4; i++) {
            inner ^= x[i] & parity[i];
        }
        for (i = 16; i > 0; i >>>= 1) {
            inner ^= inner >>> i;
        }
        inner &= 1;
        if (inner == 1) {
            return;
        }
        for (i = 0; i < 4; i++) {
            for (j = 0, work = 1; j < 32; j++, work <<= 1) {
                if ((work & parity[i]) != 0) {
                    x[i] ^= work;
                    return;
                }
            }
        }
    }

    /* 整数の種 s による初期化 */
 /*synchronized*/ public void initMt(int s) {
        x[0] = s;
        for (int p = 1; p < 1392; p++) {
            x[p] = s = 1812433253 * (s ^ (s >>> 30)) + p;
        }
        periodCertification();
    }

    Sfmt(int s) {
        initMt(s);
    }

    /* 配列 initKey による初期化 */
 /*synchronized*/ public void initMtEx(int[] initKey) {
        int r;
        int i;
        int j;
        int c;
        int keyLen = initKey.length;

        for (i = 0; i < 1392; i++) {
            x[i] = 0x8b8b8b8b;
        }
        if (keyLen + 1 > 1392) {
            c = keyLen + 1;
        } else {
            c = 1392;
        }
        r = x[0] ^ x[690] ^ x[1391];
        r = (r ^ (r >>> 27)) * 1664525;
        x[690] += r;
        r += keyLen;
        x[701] += r;
        x[0] = r;
        c--;
        for (i = 1, j = 0; j < c && j < keyLen; j++) {
            r = x[i] ^ x[(i + 690) % 1392] ^ x[(i + 1391) % 1392];
            r = (r ^ (r >>> 27)) * 1664525;
            x[(i + 690) % 1392] += r;
            r += initKey[j] + i;
            x[(i + 701) % 1392] += r;
            x[i] = r;
            i = (i + 1) % 1392;
        }
        for (; j < c; j++) {
            r = x[i] ^ x[(i + 690) % 1392] ^ x[(i + 1391) % 1392];
            r = (r ^ (r >>> 27)) * 1664525;
            x[(i + 690) % 1392] += r;
            r += i;
            x[(i + 701) % 1392] += r;
            x[i] = r;
            i = (i + 1) % 1392;
        }
        for (j = 0; j < 1392; j++) {
            r = x[i] + x[(i + 690) % 1392] + x[(i + 1391) % 1392];
            r = (r ^ (r >>> 27)) * 1566083941;
            x[(i + 690) % 1392] ^= r;
            r -= i;
            x[(i + 701) % 1392] ^= r;
            x[i] = r;
            i = (i + 1) % 1392;
        }
        periodCertification();
    }

    Sfmt(int[] initKey) {
        initMtEx(initKey);
    }

    /* 32ビット符号あり整数の乱数 */
 /*synchronized*/ public int nextMt() {
        if (index == 1392) {
            genRandAll();
            index = 0;
        }
        return x[index++];
    }

    /* ０以上 n 未満の整数乱数 */
    public int nextInt(int n) {
        double z = nextMt();
        if (z < 0) {
            z += 4294967296.0;
        }
        return (int) (n * (1.0 / 4294967296.0) * z);
    }

    /* ０以上１未満の乱数(53bit精度) */
    public double nextUnif() {
        double z = nextMt() >>> 11;
        double y = nextMt();
        if (y < 0) {
            y += 4294967296.0;
        }
        return (y * 2097152.0 + z) * (1.0 / 9007199254740992.0);
    }

    /* ０か１を返す乱数 */
 /*synchronized*/ public int nextBit() {
        if (--coinBits == -1) {
            coinBits = 31;
            return (coinSave = nextMt()) & 1;
        } else {
            return (coinSave >>>= 1) & 1;
        }
    }

    /* ０から２５５を返す乱数 */
 /*synchronized*/ public int nextByte() {
        if (--bytePos == -1) {
            bytePos = 3;
            return (byteSave = nextMt()) & 255;
        } else {
            return (byteSave >>>= 8) & 255;
        }
    }

    /* 丸め誤差のない０以上 range1 未満の整数乱数 */
 /*synchronized*/ public int nextIntEx(int range1) {
        int y1;
        int base1;
        int remain1;
        int shift1;

        if (range1 <= 0) {
            return 0;
        }
        if (range1 != range) {
            base = (range = range1);
            for (shift = 0; base <= (1 << 30) && base != 1 << 31; shift++) {
                base <<= 1;
            }
        }
        for (;;) {
            y1 = nextMt() >>> 1;
            if (y1 < base || base == 1 << 31) {
                return (y1 >>> shift);
            }
            base1 = base;
            shift1 = shift;
            y1 -= base1;
            remain1 = (1 << 31) - base1;
            for (; remain1 >= range1; remain1 -= base1) {
                for (; base1 > remain1; base1 >>>= 1) {
                    shift1--;
                }
                if (y1 < base1) {
                    return (y1 >>> shift1);
                } else {
                    y1 -= base1;
                }
            }
        }
    }

    /* 自由度νのカイ２乗分布 */
    public double nextChisq(double n) {
        return 2 * nextGamma(0.5 * n);
    }

    /* パラメータａのガンマ分布 */
    public double nextGamma(double a) {
        double t;
        double u;
        double x1;
        double y;
        if (a > 1) {
            t = Math.sqrt(2 * a - 1);
            do {
                do {
                    do {
                        x1 = 1 - nextUnif();
                        y = 2 * nextUnif() - 1;
                    } while (x1 * x1 + y * y > 1);
                    y /= x1;
                    x1 = t * y + a - 1;
                } while (x1 <= 0);
                u = (a - 1) * Math.log(x1 / (a - 1)) - t * y;
            } while (u < -50 || nextUnif() > (1 + y * y) * Math.exp(u));
        } else {
            t = 2.718281828459045235 / (a + 2.718281828459045235);
            do {
                if (nextUnif() < t) {
                    x1 = Math.pow(nextUnif(), 1 / a);
                    y = Math.exp(-x1);
                } else {
                    x1 = 1 - Math.log(1 - nextUnif());
                    y = Math.pow(x1, a - 1);
                }
            } while (nextUnif() >= y);
        }
        return x1;
    }

    /* 確率Ｐの幾何分布 */
    public int nextGeometric(double p) {
        return (int) Math.ceil(Math.log(1.0 - nextUnif()) / Math.log(1 - p));
    }

    /* 三角分布 */
    public double nextTriangle() {
        double a = nextUnif();
        double b = nextUnif();
        return a - b;
    }

    /* 平均１の指数分布 */
    public double nextExp() {
        return -Math.log(1 - nextUnif());
    }

    /* 標準正規分布(最大8.57σ) */
 /*synchronized*/ public double nextNormal() {
        if (normalSw == 0) {
            double t = Math.sqrt(-2 * Math.log(1.0 - nextUnif()));
            double u = 3.141592653589793 * 2 * nextUnif();
            normalSave = t * Math.sin(u);
            normalSw = 1;
            return t * Math.cos(u);
        } else {
            normalSw = 0;
            return normalSave;
        }
    }

    /* Ｎ次元のランダム単位ベクトル */
    public double[] nextUnitVect(int n) {
        int i;
        double r = 0;
        double[] v = new double[n];
        for (i = 0; i < n; i++) {
            v[i] = nextNormal();
            r += v[i] * v[i];
        }
        if (r == 0.0) {
            r = 1.0;
        }
        r = Math.sqrt(r);
        for (i = 0; i < n; i++) {
            v[i] /= r;
        }
        return v;
    }

    /* パラメータＮ,Ｐの２項分布 */
    public int nextBinomial(int n, double p) {
        int i;
        int r = 0;
        for (i = 0; i < n; i++) {
            if (nextUnif() < p) {
                r++;
            }
        }
        return r;
    }

    /* 相関係数Ｒの２変量正規分布 */
    public double[] nextBinormal(double r) {
        double r1;
        double r2;
        double s;
        do {
            r1 = 2 * nextUnif() - 1;
            r2 = 2 * nextUnif() - 1;
            s = r1 * r1 + r2 * r2;
        } while (s > 1 || s == 0);
        s = -Math.log(s) / s;
        r1 = Math.sqrt((1 + r) * s) * r1;
        r2 = Math.sqrt((1 - r) * s) * r2;
        return new double[]{r1 + r2, r1 - r2};
    }

    /* パラメータＡ,Ｂのベータ分布 */
    public double nextBeta(double a, double b) {
        double temp = nextGamma(a);
        return temp / (temp + nextGamma(b));
    }

    /* パラメータＮの累乗分布 */
    public double nextPower(double n) {
        return Math.pow(nextUnif(), 1.0 / (n + 1));
    }

    /* ロジスティック分布 */
    public double nextLogistic() {
        double r;
        do {
            r = nextUnif();
        } while (r == 0);
        return Math.log(r / (1 - r));
    }

    /* コーシー分布 */
    public double nextCauchy() {
        double x1;
        double y;
        do {
            x1 = 1 - nextUnif();
            y = 2 * nextUnif() - 1;
        } while (x1 * x1 + y * y > 1);
        return y / x1;
    }

    /* 自由度 n1,n2 のＦ分布 */
    public double nextFDist(double n1, double n2) {
        double nc1 = nextChisq(n1);
        double nc2 = nextChisq(n2);
        return (nc1 * n2) / (nc2 * n1);
    }

    /* 平均λのポアソン分布 */
    public int nextPoisson(double lambda) {
        int k;
        lambda = Math.exp(lambda) * nextUnif();
        for (k = 0; lambda > 1; k++) {
            lambda *= nextUnif();
        }
        return k;
    }

    /* 自由度Ｎのｔ分布 */
    public double nextTDist(double n) {
        double a;
        double b;
        double c;
        if (n <= 2) {
            do {
                a = nextChisq(n);
            } while (a == 0);
            return nextNormal() / Math.sqrt(a / n);
        }
        do {
            a = nextNormal();
            b = a * a / (n - 2);
            c = Math.log(1 - nextUnif()) / (1 - 0.5 * n);
        } while (Math.exp(-b - c) > 1 - b);
        return a / Math.sqrt((1 - 2.0 / n) * (1 - b));
    }

    /* パラメータαのワイブル分布 */
    public double nextWeibull(double alpha) {
        return Math.pow(-Math.log(1 - nextUnif()), 1 / alpha);
    }
}
