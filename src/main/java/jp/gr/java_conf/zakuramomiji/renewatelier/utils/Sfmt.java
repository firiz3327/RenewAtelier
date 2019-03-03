package jp.gr.java_conf.zakuramomiji.renewatelier.utils;

/*
Sfmt.java 乱数ライブラリ(44497)
coded by isaku@pb4.so-net.ne.jp
 */
final class Sfmt {

    int index;
    int coin_bits;
    /* NextBit での残りのビット */
    int coin_save;
    /* NextBit での値保持 */
    int byte_pos;
    /* NextByte で使用したバイト数 */
    int byte_save;
    /* NextByte での値保持 */
    int range;
    /* NextIntEx で前回の範囲 */
    int base;
    /* NextIntEx で前回の基準値 */
    int shift;
    /* NextIntEx で前回のシフト数 */
    int normal_sw;
    /* NextNormal で残りを持っている */
    double normal_save;
    /* NextNormal の残りの値 */
    protected final int[] x = new int[1392];

    /* 状態テーブル */
    protected int[] GetParity() {
        return new int[]{0x00000001, 0x00000000, 0xa3ac4000, 0xecc1327a};
    }

    public String IdString() {
        return "SFMT-44497:330-5-3-9-3:effffffb-dfbebfff-bfbf7bef-9ffd7bff";
    }

    public void gen_rand_all() {
        int a = 0, b = 1320, c = 1384, d = 1388, y;
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

    void period_certification() {
        int work, inner = 0;
        int i, j;
        int[] parity = GetParity();

        index = 1392;
        range = 0;
        normal_sw = 0;
        coin_bits = 0;
        byte_pos = 0;
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
 /*synchronized*/ public void InitMt(int s) {
        x[0] = s;
        for (int p = 1; p < 1392; p++) {
            x[p] = s = 1812433253 * (s ^ (s >>> 30)) + p;
        }
        period_certification();
    }

    Sfmt(int s) {
        InitMt(s);
    }

    /* 配列 init_key による初期化 */
 /*synchronized*/ public void InitMtEx(int[] init_key) {
        int r, i, j, c, key_len = init_key.length;

        for (i = 0; i < 1392; i++) {
            x[i] = 0x8b8b8b8b;
        }
        if (key_len + 1 > 1392) {
            c = key_len + 1;
        } else {
            c = 1392;
        }
        r = x[0] ^ x[690] ^ x[1391];
        r = (r ^ (r >>> 27)) * 1664525;
        x[690] += r;
        r += key_len;
        x[701] += r;
        x[0] = r;
        c--;
        for (i = 1, j = 0; j < c && j < key_len; j++) {
            r = x[i] ^ x[(i + 690) % 1392] ^ x[(i + 1391) % 1392];
            r = (r ^ (r >>> 27)) * 1664525;
            x[(i + 690) % 1392] += r;
            r += init_key[j] + i;
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
        period_certification();
    }

    Sfmt(int[] init_key) {
        InitMtEx(init_key);
    }

    /* 32ビット符号あり整数の乱数 */
 /*synchronized*/ public int NextMt() {
        if (index == 1392) {
            gen_rand_all();
            index = 0;
        }
        return x[index++];
    }

    /* ０以上 n 未満の整数乱数 */
    public int NextInt(int n) {
        double z = NextMt();
        if (z < 0) {
            z += 4294967296.0;
        }
        return (int) (n * (1.0 / 4294967296.0) * z);
    }

    /* ０以上１未満の乱数(53bit精度) */
    public double NextUnif() {
        double z = NextMt() >>> 11, y = NextMt();
        if (y < 0) {
            y += 4294967296.0;
        }
        return (y * 2097152.0 + z) * (1.0 / 9007199254740992.0);
    }

    /* ０か１を返す乱数 */
 /*synchronized*/ public int NextBit() {
        if (--coin_bits == -1) {
            coin_bits = 31;
            return (coin_save = NextMt()) & 1;
        } else {
            return (coin_save >>>= 1) & 1;
        }
    }

    /* ０から２５５を返す乱数 */
 /*synchronized*/ public int NextByte() {
        if (--byte_pos == -1) {
            byte_pos = 3;
            return (byte_save = NextMt()) & 255;
        } else {
            return (byte_save >>>= 8) & 255;
        }
    }

    /* 丸め誤差のない０以上 range_ 未満の整数乱数 */
 /*synchronized*/ public int NextIntEx(int range_) {
        int y_, base_, remain_;
        int shift_;

        if (range_ <= 0) {
            return 0;
        }
        if (range_ != range) {
            base = (range = range_);
            for (shift = 0; base <= (1 << 30) && base != 1 << 31; shift++) {
                base <<= 1;
            }
        }
        for (;;) {
            y_ = NextMt() >>> 1;
            if (y_ < base || base == 1 << 31) {
                return (y_ >>> shift);
            }
            base_ = base;
            shift_ = shift;
            y_ -= base_;
            remain_ = (1 << 31) - base_;
            for (; remain_ >= range_; remain_ -= base_) {
                for (; base_ > remain_; base_ >>>= 1) {
                    shift_--;
                }
                if (y_ < base_) {
                    return (y_ >>> shift_);
                } else {
                    y_ -= base_;
                }
            }
        }
    }

    /* 自由度νのカイ２乗分布 */
    public double NextChisq(double n) {
        return 2 * NextGamma(0.5 * n);
    }

    /* パラメータａのガンマ分布 */
    public double NextGamma(double a) {
        double t, u, X, y;
        if (a > 1) {
            t = Math.sqrt(2 * a - 1);
            do {
                do {
                    do {
                        X = 1 - NextUnif();
                        y = 2 * NextUnif() - 1;
                    } while (X * X + y * y > 1);
                    y /= X;
                    X = t * y + a - 1;
                } while (X <= 0);
                u = (a - 1) * Math.log(X / (a - 1)) - t * y;
            } while (u < -50 || NextUnif() > (1 + y * y) * Math.exp(u));
        } else {
            t = 2.718281828459045235 / (a + 2.718281828459045235);
            do {
                if (NextUnif() < t) {
                    X = Math.pow(NextUnif(), 1 / a);
                    y = Math.exp(-X);
                } else {
                    X = 1 - Math.log(1 - NextUnif());
                    y = Math.pow(X, a - 1);
                }
            } while (NextUnif() >= y);
        }
        return X;
    }

    /* 確率Ｐの幾何分布 */
    public int NextGeometric(double p) {
        return (int) Math.ceil(Math.log(1.0 - NextUnif()) / Math.log(1 - p));
    }

    /* 三角分布 */
    public double NextTriangle() {
        double a = NextUnif(), b = NextUnif();
        return a - b;
    }

    /* 平均１の指数分布 */
    public double NextExp() {
        return -Math.log(1 - NextUnif());
    }

    /* 標準正規分布(最大8.57σ) */
 /*synchronized*/ public double NextNormal() {
        if (normal_sw == 0) {
            double t = Math.sqrt(-2 * Math.log(1.0 - NextUnif()));
            double u = 3.141592653589793 * 2 * NextUnif();
            normal_save = t * Math.sin(u);
            normal_sw = 1;
            return t * Math.cos(u);
        } else {
            normal_sw = 0;
            return normal_save;
        }
    }

    /* Ｎ次元のランダム単位ベクトル */
    public double[] NextUnitVect(int n) {
        int i;
        double r = 0;
        double[] v = new double[n];
        for (i = 0; i < n; i++) {
            v[i] = NextNormal();
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
    public int NextBinomial(int n, double p) {
        int i, r = 0;
        for (i = 0; i < n; i++) {
            if (NextUnif() < p) {
                r++;
            }
        }
        return r;
    }

    /* 相関係数Ｒの２変量正規分布 */
    public double[] NextBinormal(double r) {
        double r1, r2, s;
        do {
            r1 = 2 * NextUnif() - 1;
            r2 = 2 * NextUnif() - 1;
            s = r1 * r1 + r2 * r2;
        } while (s > 1 || s == 0);
        s = -Math.log(s) / s;
        r1 = Math.sqrt((1 + r) * s) * r1;
        r2 = Math.sqrt((1 - r) * s) * r2;
        return new double[]{r1 + r2, r1 - r2};
    }

    /* パラメータＡ,Ｂのベータ分布 */
    public double NextBeta(double a, double b) {
        double temp = NextGamma(a);
        return temp / (temp + NextGamma(b));
    }

    /* パラメータＮの累乗分布 */
    public double NextPower(double n) {
        return Math.pow(NextUnif(), 1.0 / (n + 1));
    }

    /* ロジスティック分布 */
    public double NextLogistic() {
        double r;
        do {
            r = NextUnif();
        } while (r == 0);
        return Math.log(r / (1 - r));
    }

    /* コーシー分布 */
    public double NextCauchy() {
        double x, y;
        do {
            x = 1 - NextUnif();
            y = 2 * NextUnif() - 1;
        } while (x * x + y * y > 1);
        return y / x;
    }

    /* 自由度 n1,n2 のＦ分布 */
    public double NextFDist(double n1, double n2) {
        double nc1 = NextChisq(n1), nc2 = NextChisq(n2);
        return (nc1 * n2) / (nc2 * n1);
    }

    /* 平均λのポアソン分布 */
    public int NextPoisson(double lambda) {
        int k;
        lambda = Math.exp(lambda) * NextUnif();
        for (k = 0; lambda > 1; k++) {
            lambda *= NextUnif();
        }
        return k;
    }

    /* 自由度Ｎのｔ分布 */
    public double NextTDist(double n) {
        double a, b, c;
        if (n <= 2) {
            do {
                a = NextChisq(n);
            } while (a == 0);
            return NextNormal() / Math.sqrt(a / n);
        }
        do {
            a = NextNormal();
            b = a * a / (n - 2);
            c = Math.log(1 - NextUnif()) / (1 - 0.5 * n);
        } while (Math.exp(-b - c) > 1 - b);
        return a / Math.sqrt((1 - 2.0 / n) * (1 - b));
    }

    /* パラメータαのワイブル分布 */
    public double NextWeibull(double alpha) {
        return Math.pow(-Math.log(1 - NextUnif()), 1 / alpha);
    }
}
