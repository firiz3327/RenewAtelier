package net.firiz.renewatelier.entity.horse;

public enum HorseTier {
    TIER_1(
            1, false, 10,
            0.1, 0.01, // 0.1 ~ 0.2
            0.4, 0.02 // 0.4 ~ 0.6
    ),
    TIER_2(
            2, false, 15,
            0.1, 0.008, // 0.1 ~ 0.22
            0.4, 0.02 // 0.4 ~ 0.6
    ),
    TIER_3(
            3, false, 20,
            0.12, 0.006, // 0.12 ~ 0.24
            0.5, 0.01 // 0.5 ~ 0.7
    ),
    TIER_4(
            4, false, 25,
            0.12, 0.006, // 0.12 ~ 0.27
            0.5, 0.01 // 0.5 ~ 0.7
    ),
    TIER_5(
            5, false, 30,
            0.18, 0.004, // 0.2 ~ 0.3
            0.5, 0.008 // 0.5 ~ 0.74
    ),
    TIER_6(
            6, true, 35,
            0.2, 0.004, // 0.2 ~ 0.34
            0.5, 0.008 // 0.5 ~ 0.74
    ),
    TIER_7(
            7, true, 40,
            0.22, 0.0035, // 0.22 ~ 0.36
            0.6, 0.005 // 0.6 ~ 0.8
    ),
    TIER_8(
            8, true, 40,
            0.22, 0.004, // 0.22 ~ 0.38
            0.6, 0.005 // 0.6 ~ 0.8
    );

    private final int tier;
    private final boolean rare;
    private final int maxLevel;
    private final double baseSpeed;
    private final double speedMag;
    private final double baseJump;
    private final double jumpMag;

    HorseTier(int tier, boolean rare, int maxLevel, double baseSpeed, double speedMag, double baseJump, double jumpMag) {
        this.tier = tier;
        this.rare = rare;
        this.maxLevel = maxLevel;
        this.baseSpeed = baseSpeed;
        this.speedMag = speedMag;
        this.baseJump = baseJump;
        this.jumpMag = jumpMag;
    }

    public boolean isRare() {
        return rare;
    }

    public double getSpeed(int level) {
        return baseSpeed + (level * speedMag);
    }

    public double getJump(int level) {
        return baseJump + (level * jumpMag);
    }

    public int getTier() {
        return tier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
