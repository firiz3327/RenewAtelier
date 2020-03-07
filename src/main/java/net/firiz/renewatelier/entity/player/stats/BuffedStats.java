package net.firiz.renewatelier.entity.player.stats;

import net.firiz.renewatelier.entity.CalcStatType;

public class BuffedStats {

    private final CharStats charStats;
    private final EquipStats equipStats;
    private int level;

    // バフ増加値
    private int levelB;
    private int maxHpB;
    private int maxMpB;
    private int atkB;
    private int defB;
    private int speedB;

    BuffedStats(CharStats charStats, EquipStats equipStats, int level) {
        this.charStats = charStats;
        this.equipStats = equipStats;
        this.level = level;
        update();
    }

    public void update() {
        this.levelB = CalcStatType.LEVEL.getCharBuffStats(charStats, level);
        this.maxHpB = CalcStatType.HP.getCharBuffStats(charStats, equipStats.getMaxHp());
        this.maxMpB = CalcStatType.MP.getCharBuffStats(charStats, equipStats.getMaxMp());
        this.atkB = CalcStatType.ATK.getCharBuffStats(charStats, equipStats.getAtk());
        this.defB = CalcStatType.DEF.getCharBuffStats(charStats, equipStats.getDef());
        this.speedB = CalcStatType.SPEED.getCharBuffStats(charStats, equipStats.getSpeed());
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
