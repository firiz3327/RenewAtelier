package net.firiz.renewatelier.entity.player.stats;

public class BuffedStats {

    private final CharStats charStats;
    private final CharacteristicStats equipStats;
    private int level;

    // バフ増加値
    private int levelB;
    private int maxHpB;
    private int maxMpB;
    private int atkB;
    private int defB;
    private int speedB;

    BuffedStats(CharStats charStats, CharacteristicStats equipStats, int level) {
        this.charStats = charStats;
        this.equipStats = equipStats;
        this.level = level;
        update();
    }

    public void update() {
        this.levelB = CharStatType.LEVEL.getBuffStats(charStats, level);
        this.maxHpB = CharStatType.HP.getBuffStats(charStats, equipStats.getMaxHp());
        this.maxMpB = CharStatType.MP.getBuffStats(charStats, equipStats.getMaxMp());
        this.atkB = CharStatType.ATK.getBuffStats(charStats, equipStats.getAtk());
        this.defB = CharStatType.DEF.getBuffStats(charStats, equipStats.getDef());
        this.speedB = CharStatType.SPEED.getBuffStats(charStats, equipStats.getSpeed());
    }

    public int getLevel() {
        return level + levelB;
    }

    public int getMaxHp() {
        return equipStats.getMaxHp() + maxHpB;
    }

    public int getMaxMp() {
        return equipStats.getMaxMp() + maxMpB;
    }

    public int getAtk() {
        return equipStats.getAtk() + atkB;
    }

    public int getDef() {
        return equipStats.getDef() + defB;
    }

    public int getSpeed() {
        return equipStats.getSpeed() + speedB;
    }

}
