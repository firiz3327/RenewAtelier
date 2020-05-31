package net.firiz.renewatelier.tools;

public class SublayerBitmasks {

    public static void main(String[] args) {
        final byte[] sublayerBitmasks = new byte[]{
                0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x30, 0x40
        };
        byte all_flag = 0;
        for (final byte bitmask : sublayerBitmasks) {
            all_flag |= bitmask;
        }
        System.out.println(all_flag);
    }

}
